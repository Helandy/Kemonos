package su.afk.kemonos.network.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import su.afk.kemonos.auth.domain.repository.AuthSessionProvider
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

internal class AuthCookieInterceptor @Inject constructor(
    private val authSessionProvider: AuthSessionProvider,
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

        val session = runBlocking {
            authSessionProvider.getSession(currentSite)
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
