package su.afk.kemonos.posts.data

import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.posts.api.apiCheck.ApiCheckResult
import su.afk.kemonos.posts.data.api.PostsApi
import javax.inject.Inject

interface ICheckApiRepository {
    suspend fun getApiCheck(): ApiCheckResult
}

internal class CheckApiRepository @Inject constructor(
    private val api: PostsApi,
    private val errorHandler: IErrorHandlerUseCase,
) : ICheckApiRepository {

    /** проверка доступности Api сайта */
    override suspend fun getApiCheck(): ApiCheckResult {
        return try {
            val resp = api.getPosts()

            if (resp.isSuccessful) {
                ApiCheckResult(success = true)
            } else {
                val code = resp.code()
                val body = resp.errorBody()?.string()
                ApiCheckResult(
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
            ApiCheckResult(
                success = false,
                error = errorHandler.parse(t)
            )
        }
    }
}