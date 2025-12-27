package su.afk.kemonos.core.api.domain.net.helpers

import retrofit2.HttpException
import retrofit2.Response

inline fun <T, R> Response<T>.call(mapper: (T) -> R): R {
    if (!isSuccessful) throw HttpException(this)

    val body = body() ?: throw IllegalStateException(
        "Response body is empty. code=${code()} message=${message()}"
    )

    return mapper(body)
}

inline fun <T, R> Response<T>.callOrNull(mapper: (T) -> R): R? =
    try {
        call(mapper)
    } catch (_: Exception) {
        null
    }

fun Response<Unit>.successOrFalse(): Boolean = isSuccessful