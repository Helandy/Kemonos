package su.afk.kemonos.profile.data.remote

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.profile.di.PawchiveAuthOkHttp
import su.afk.kemonos.utils.withIo
import javax.inject.Inject

internal sealed interface PawchiveLoginOutcome {
    data class Success(val session: String) : PawchiveLoginOutcome
    data class Failure(val error: ErrorItem) : PawchiveLoginOutcome
}

/**
 * Вход на pawchive.
 *
 * У источника нет JSON-API входа — `/api/v1/authentication/login` отвечает 404.
 * Единственный способ получить сессию: отправить форму на `/account/login`,
 * как это делает сайт.
 */
internal class PawchiveAuthRemote @Inject constructor(
    @param:PawchiveAuthOkHttp private val client: OkHttpClient,
    private val domainResolver: IDomainResolver,
) {

    suspend fun login(
        site: SelectedSite,
        username: String,
        password: String,
    ): PawchiveLoginOutcome = withIo {
        val hosts = domainResolver.hostConfig(site)
        val loginUrl = "${hosts.rootUrl.trimEnd('/')}$LOGIN_PATH"

        val form = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("location", REDIRECT_LOCATION)
            .build()

        val response = client
            .newCall(Request.Builder().url(loginUrl).post(form).build())
            .execute()

        val session = response.use { it.headers.values("Set-Cookie").pawchiveSessionCookie() }
            ?: return@withIo PawchiveLoginOutcome.Failure(
                ErrorItem(
                    title = LOGIN_FAILED,
                    message = "Session cookie is missing.",
                    code = null,
                    url = loginUrl,
                    method = "POST",
                )
            )

        /**
         * Куку сайт ставит и на неудачный вход — там лежит лишь flash-сообщение.
         * Поэтому успех проверяем тем же эндпоинтом, ради которого вход и нужен.
         */
        verify(site = site, session = session, loginUrl = loginUrl)
    }

    private fun verify(
        site: SelectedSite,
        session: String,
        loginUrl: String,
    ): PawchiveLoginOutcome {
        val apiBaseUrl = domainResolver.hostConfig(site).apiBaseUrl.trimEnd('/')
        val probeUrl = "$apiBaseUrl$FAVORITES_PROBE_PATH"

        val probe = client.newCall(
            Request.Builder()
                .url(probeUrl)
                .header("Cookie", "session=$session")
                .header("Accept", "text/css")
                .build()
        ).execute()

        val code = probe.code
        probe.close()

        return when {
            code in 200..299 -> PawchiveLoginOutcome.Success(session)

            code == 401 || code == 403 -> PawchiveLoginOutcome.Failure(
                ErrorItem(
                    title = LOGIN_FAILED,
                    message = session.pawchiveFlashMessage() ?: "Invalid username or password.",
                    code = code,
                    url = loginUrl,
                    method = "POST",
                )
            )

            else -> PawchiveLoginOutcome.Failure(
                ErrorItem(
                    title = LOGIN_FAILED,
                    message = "Could not confirm the session.",
                    code = code,
                    url = probeUrl,
                    method = "GET",
                )
            )
        }
    }

    private companion object {
        const val LOGIN_PATH = "/account/login"
        const val REDIRECT_LOCATION = "/artists"
        const val FAVORITES_PROBE_PATH = "/v1/account/favorites?type=artist"
        const val LOGIN_FAILED = "Login failed"
    }
}
