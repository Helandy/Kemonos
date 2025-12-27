package su.afk.kemonos.core.network.intercaptiors.creators

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import su.afk.kemonos.core.api.domain.net.intercaptiors.HeaderText
import javax.inject.Inject

class CreatorsTextInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        /** получаем Invocation, в котором хранятся все аннотации Retrofit */
        val invocation = request.tag(Invocation::class.java)

        val isHeaderText = invocation
            ?.method()
            ?.isAnnotationPresent(HeaderText::class.java) == true

        if (!isHeaderText) {
            /** метод не помечен → пропускаем как есть */
            return chain.proceed(request)
        }

        /** модифицируем запрос */
        val newRequest = request.newBuilder()
            .header("Accept", "text/css")
            .build()

        return chain.proceed(newRequest)
    }
}