package su.afk.kemonos.posts.data

import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.posts.api.apiCheck.ApiCheckForAllSitesResult
import su.afk.kemonos.posts.api.apiCheck.SingleSiteCheck
import su.afk.kemonos.posts.data.api.PostsApi
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.withSite
import javax.inject.Inject

interface ICheckApiRepository {
    suspend fun getApiCheckForAllSites(): ApiCheckForAllSitesResult
}

internal class CheckApiRepository @Inject constructor(
    private val api: PostsApi,
    private val selectedSite: ISelectedSiteUseCase,
    private val errorHandler: IErrorHandlerUseCase,
) : ICheckApiRepository {

    /** проверка доступности Api сайта */
    override suspend fun getApiCheckForAllSites(): ApiCheckForAllSitesResult {
        val kemono = checkSite(SelectedSite.K)
        val coomer = checkSite(SelectedSite.C)

        return ApiCheckForAllSitesResult(
            kemono = kemono,
            coomer = coomer,
        )
    }

    private suspend fun checkSite(site: SelectedSite): SingleSiteCheck {
        return try {
            val resp = selectedSite.withSite(site) {
                api.getPosts()
            }

            if (resp.isSuccessful) {
                SingleSiteCheck(site = site, success = true)
            } else {
                val code = resp.code()
                val body = resp.errorBody()?.string()
                SingleSiteCheck(
                    site = site,
                    success = false,
                    error = ErrorItem(
                        title = "HTTP error ($code)",
                        message = body?.takeIf { it.isNotBlank() } ?: "Empty response body",
                        code = code,
                        body = body
                    )
                )
            }
        } catch (t: Throwable) {
            SingleSiteCheck(
                site = site,
                success = false,
                error = errorHandler.parse(t)
            )
        }
    }
}