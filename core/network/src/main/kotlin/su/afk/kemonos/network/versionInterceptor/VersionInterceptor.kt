package su.afk.kemonos.network.versionInterceptor

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import su.afk.kemonos.network.BuildConfig
import javax.inject.Inject

class VersionInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        /** получаем Invocation, в котором хранятся все аннотации Retrofit */
        val invocation = request.tag(Invocation::class.java)

        val isHeaderText = invocation
            ?.method()
            ?.isAnnotationPresent(VersionHeader::class.java) == true

        if (!isHeaderText) {
            /** метод не помечен → пропускаем как есть */
            return chain.proceed(request)
        }

        /** модифицируем запрос */
        val newRequest = request.newBuilder()
            .header("Kemonos", BuildConfig.VERSION_NAME)
            .build()

        return chain.proceed(newRequest)
    }
}
