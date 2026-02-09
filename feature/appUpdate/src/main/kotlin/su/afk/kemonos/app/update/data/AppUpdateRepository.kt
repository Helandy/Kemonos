package su.afk.kemonos.app.update.data

import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.app.update.data.api.GitHubReleasesApi
import su.afk.kemonos.app.update.domain.repository.IAppUpdateRepository
import javax.inject.Inject

internal class AppUpdateRepository @Inject constructor(
    private val api: GitHubReleasesApi
) : IAppUpdateRepository {

    override suspend fun getLatestVersion(): Result<AppUpdateInfo> = runCatching {
        val response = api.getLatestRelease()

        if (!response.isSuccessful) error("GitHub http=${response.code()}")
        val body = response.body() ?: error("Empty body")

        AppUpdateInfo(
            latestVersionName = body.tagName,
            changelog = body.body.orEmpty(),
            releaseUrl = body.htmlUrl,
        )
    }.onFailure {
        error(it.message ?: "Unknown error")
    }
}
