package su.afk.kemonos.creatorPost.data.repository.helper

import kotlinx.coroutines.CancellationException

internal suspend inline fun <T> cacheFirstOrNetwork(
    crossinline freshCache: suspend () -> T?,
    crossinline network: suspend () -> T,
    crossinline saveToCache: suspend (T) -> Unit,
    crossinline staleCache: suspend () -> T?,
): T {
    freshCache()?.let { return it }

    return try {
        network().also { saveToCache(it) }
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        staleCache() ?: throw t
    }
}
