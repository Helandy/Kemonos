package su.afk.kemonos.core.network.intercaptiors.api

import okhttp3.Interceptor
import okhttp3.Response

class ReplaceBaseUrlInterceptor(
    private val provider: BaseUrlProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val base = provider.get()
        val old = req.url

        /** Меняем только scheme/host/port, не трогая path/query */
        val newUrl = old.newBuilder()
            .scheme(base.scheme)
            .host(base.host)
            .port(base.port)
            .build()

        return chain.proceed(req.newBuilder().url(newUrl).build())
    }
}