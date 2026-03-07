package su.afk.kemonos.creatorPost.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import su.afk.kemonos.creatorPost.data.dto.file.FileInfoRequestDto
import su.afk.kemonos.creatorPost.data.dto.file.FileInfoResponseDto
import su.afk.kemonos.network.versionInterceptor.VersionHeader

internal interface FileInfoApi {

    @POST("/api/file/info")
    @VersionHeader
    suspend fun getFileInfo(
        @Body data: FileInfoRequestDto,
    ): FileInfoResponseDto
}
