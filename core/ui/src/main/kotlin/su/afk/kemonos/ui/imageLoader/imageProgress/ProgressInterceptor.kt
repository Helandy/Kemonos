package su.afk.kemonos.ui.imageLoader.imageProgress

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer

const val IMAGE_PROGRESS_REQUEST_ID_HEADER = "X-Kemonos-Image-ReqId"

class ProgressInterceptor(
    private val store: ImageProgressStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val body = response.body

        val key = request.header(IMAGE_PROGRESS_REQUEST_ID_HEADER)
            ?: request.url.toString()

        return response.newBuilder()
            .body(ProgressResponseBody(key, body, store))
            .build()
    }
}

private class ProgressResponseBody(
    private val key: String,
    private val delegate: ResponseBody,
    private val store: ImageProgressStore,
) : ResponseBody() {
    override fun contentType() = delegate.contentType()
    override fun contentLength() = delegate.contentLength()

    override fun source(): BufferedSource {
        val source = delegate.source()
        return object : ForwardingSource(source) {
            var total = 0L
            val length = contentLength()

            override fun read(sink: Buffer, byteCount: Long): Long {
                val read = super.read(sink, byteCount)
                if (read != -1L) {
                    total += read
                    store.update(key, total, length)
                }
                return read
            }
        }.buffer()
    }
}