package su.afk.kemonos.core.network.intercaptiors.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import su.afk.kemonos.auth.IAuthLocalDataSource
import su.afk.kemonos.core.api.domain.net.intercaptiors.auth.AuthCookie
import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.domain.SelectedSite
import javax.inject.Inject

internal class AuthCookieInterceptor @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
    private val selectedSiteProvider: ISelectedSiteUseCase,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        /** получаем Retrofit Invocation */
        val invocation = request.tag(Invocation::class.java)

        /** если метод НЕ помечен @AuthCookie → пропускаем */
        val hasAuthCookie = invocation
            ?.method()
            ?.isAnnotationPresent(AuthCookie::class.java) == true

        if (!hasAuthCookie) {
            return chain.proceed(request)
        }

        /** какой сайт сейчас выбран (Kemono / Coomer) */
        val currentSite: SelectedSite = selectedSiteProvider.getSite()

        /** достаём authState синхронно */
        val authState = runBlocking {
            authLocalDataSource.authState.first()
        }

        /** выбираем нужную сессию по сайту */
        val session = when (currentSite) {
            SelectedSite.K -> authState.kemono.session
            SelectedSite.C -> authState.coomer.session
        }

        if (session.isNullOrBlank()) {
            /** нет сессии для нужного сайта → отправляем запрос без куки */
            return chain.proceed(request)
        }

        val newRequest = request.newBuilder()
            .addHeader("Cookie", "session=$session")
            .build()

        return chain.proceed(newRequest)
    }
}