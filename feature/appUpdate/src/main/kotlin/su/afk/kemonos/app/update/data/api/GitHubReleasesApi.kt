package su.afk.kemonos.app.update.data.api

import retrofit2.Response
import retrofit2.http.GET
import su.afk.kemonos.app.update.data.dto.GitHubReleaseDto

internal interface GitHubReleasesApi {

    @GET("repos/helandy/kemonos/releases/latest")
    suspend fun getLatestRelease(): Response<GitHubReleaseDto>
}