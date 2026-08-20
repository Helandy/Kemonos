package su.afk.kemonos.posts.data

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.posts.api.apiCheck.ApiCheckForAllSitesResult
import su.afk.kemonos.posts.api.apiCheck.SingleSiteCheck
import su.afk.kemonos.posts.data.api.PostsApi
import su.afk.kemonos.posts.domain.repository.ICheckApiRepository
import javax.inject.Inject

internal class CheckApiRepository @Inject constructor(
    private val apis: Map<SelectedSite, @JvmSuppressWildcards PostsApi>,
    private val errorHandler: IErrorHandlerUseCase,
) : ICheckApiRepository {

    override suspend fun getApiCheckForSites(sitesToCheck: Set<SelectedSite>): ApiCheckForAllSitesResult {
        val checks = SelectedSite.entries.associateWith { site ->
            if (site in sitesToCheck) checkSite(site)
            else SingleSiteCheck(site = site, success = true)
        }
        return ApiCheckForAllSitesResult(checks)
    }

    private suspend fun checkSite(site: SelectedSite): SingleSiteCheck {
        return try {
            val api = apis.getValue(site)
            val response = when (site) {
                SelectedSite.O -> api.getOnlyHavenPosts()
                SelectedSite.P -> api.getPawchivePosts()
                else -> api.getPosts()
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
