package su.afk.kemonos.ui.imageLoader

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.system.measureTimeMillis

private const val COIL_LOG_TAG = "CoilHttp"

internal class CoilRequestLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d(COIL_LOG_TAG, "-> ${request.method} ${request.url}")

        var response: Response? = null
        val tookMs = try {
            measureTimeMillis {
                response = chain.proceed(request)
            }
        } catch (e: IOException) {
            Log.e(
                COIL_LOG_TAG,
                "<- FAIL ${request.method} ${request.url}: ${e.javaClass.simpleName}: ${e.message}",
                e,
            )
            throw e
        }

        val result = checkNotNull(response)
        val contentType = result.header("Content-Type").orEmpty()
        val contentLength = result.header("Content-Length").orEmpty()
        Log.d(
            COIL_LOG_TAG,
            "<- ${result.code} ${request.method} ${request.url} " +
                    "source=${result.responseSource()} took=${tookMs}ms " +
                    "type=$contentType length=$contentLength",
        )
        return result
    }
}

private fun Response.responseSource(): String = when {
    networkResponse != null && cacheResponse != null -> "conditional-cache"
    networkResponse != null -> "network"
    cacheResponse != null -> "cache"
    else -> "unknown"
}
