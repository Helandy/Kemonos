package su.afk.kemonos.posts.data

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.posts.api.apiCheck.ApiCheckForAllSitesResult
import su.afk.kemonos.posts.api.apiCheck.SingleSiteCheck
import su.afk.kemonos.posts.data.api.PostsApi
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.withSite
import javax.inject.Inject

interface ICheckApiRepository {
    suspend fun getApiCheckForSites(sitesToCheck: Set<SelectedSite>): ApiCheckForAllSitesResult
}

internal class CheckApiRepository @Inject constructor(
    private val api: PostsApi,
    private val selectedSite: ISelectedSiteUseCase,
    private val errorHandler: IErrorHandlerUseCase,
) : ICheckApiRepository {

    override suspend fun getApiCheckForSites(sitesToCheck: Set<SelectedSite>): ApiCheckForAllSitesResult {
        val kemono = if (SelectedSite.K in sitesToCheck) checkSite(SelectedSite.K)
        else SingleSiteCheck(site = SelectedSite.K, success = true)

        val coomer = if (SelectedSite.C in sitesToCheck) checkSite(SelectedSite.C)
        else SingleSiteCheck(site = SelectedSite.C, success = true)

        return ApiCheckForAllSitesResult(kemono = kemono, coomer = coomer)
    }

    private suspend fun checkSite(site: SelectedSite): SingleSiteCheck {
        return try {
            val response = selectedSite.withSite(site) {
                api.getPosts()
            }

            if (response.isSuccessful) {
                SingleSiteCheck(site = site, success = true)
            } else {
                val code = response.code()
                val body = response.errorBody()?.string()
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
            SingleSiteCheck(site = site, success = false, error = errorHandler.parse(t))
        }
    }
}