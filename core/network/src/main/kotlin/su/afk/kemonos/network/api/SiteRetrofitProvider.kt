package su.afk.kemonos.network.api

import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import su.afk.kemonos.auth.domain.repository.AuthSessionProvider
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.network.auth.AuthCookieInterceptor
import su.afk.kemonos.network.di.ApiOkHttp
import su.afk.kemonos.network.textInterceptor.TextInterceptor
import su.afk.kemonos.preferences.UrlPrefs
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Retrofit, жёстко привязанный к источнику.
 *
 * Основной клиент ходит на «выбранный сейчас» сайт и потому хранит хост в общем
 * изменяемом состоянии. Обращаться так к другому источнику нельзя: смена хоста
 * доезжает до клиента асинхронно, а пока она едет, запрос уходит на прежний хост —
 * и наоборот, временная подмена уводит туда параллельные запросы.
 *
 * Здесь у каждого источника свой провайдер адреса и своя session-кука, поэтому
 * кросс-сайтовые вызовы не трогают глобальное состояние и могут идти параллельно.
 * Пул соединений и диспетчер переиспользуются от общего клиента.
 */
@Singleton
class SiteRetrofitProvider @Inject constructor(
    @param:Named("AppScope") private val scope: CoroutineScope,
    private val prefs: UrlPrefs,
    @param:ApiOkHttp private val sharedClient: OkHttpClient,
    private val authSessionProvider: AuthSessionProvider,
    private val logging: HttpLoggingInterceptor,
    private val textInterceptor: TextInterceptor,
) {

    private val bySite: Map<SelectedSite, Retrofit> =
        SelectedSite.entries.associateWith(::buildRetrofit)

    fun retrofit(site: SelectedSite): Retrofit = bySite.getValue(site)

    /** Реализация API для каждого источника. */
    fun <T> createApis(service: Class<T>): Map<SelectedSite, T> =
        bySite.mapValues { (_, retrofit) -> retrofit.create(service) }

    private fun buildRetrofit(site: SelectedSite): Retrofit {
        val siteUrl = prefs.siteUrl(site)
        val baseUrlProvider = FlowBaseUrlProvider(
            scope = scope,
            initialUrl = siteUrl.value,
            urlFlow = siteUrl,
        )

        val client = sharedClient.newBuilder()
            .apply {
                /** Перехватчики общего клиента завязаны на выбранный сайт — ставим свои. */
                interceptors().clear()
                addInterceptor(ReplaceBaseUrlInterceptor(baseUrlProvider))
                addInterceptor(AuthCookieInterceptor(authSessionProvider) { site })
                addInterceptor(textInterceptor)
                addInterceptor(logging)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://placeholder/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
