package su.afk.kemonos.network.util

import retrofit2.HttpException

fun Throwable.isClientError4xx(): Boolean {
    val http = this as? HttpException ?: return false
    return http.code() in 400..499
}