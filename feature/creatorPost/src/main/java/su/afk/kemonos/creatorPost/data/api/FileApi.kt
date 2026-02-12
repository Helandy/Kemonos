package su.afk.kemonos.creatorPost.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import su.afk.kemonos.creatorPost.data.dto.file.FileByHashResponseDto
import su.afk.kemonos.creatorPost.data.dto.file.FileByPathResponseDto
import su.afk.kemonos.network.textInterceptor.HeaderText

internal interface FileApi {

    @HeaderText
    @GET("v2/file/{file_hash}")
    suspend fun getFileByHash(
        @Path("file_hash") fileHash: String,
    ): Response<FileByHashResponseDto>

    @HeaderText
    @GET("v2/file/{file_hash}")
    suspend fun getFileByPath(
        @Path(value = "file_hash", encoded = true) path: String,
    ): Response<FileByPathResponseDto>
}
