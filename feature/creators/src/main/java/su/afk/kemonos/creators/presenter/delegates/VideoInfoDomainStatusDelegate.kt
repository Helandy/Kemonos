package su.afk.kemonos.creators.presenter.delegates

import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import su.afk.kemonos.utils.DispatcherProvider
import javax.inject.Inject
import javax.inject.Named

internal class VideoInfoDomainStatusDelegate @Inject constructor(
    @param:Named("VideoInfoClientRemote") private val client: OkHttpClient,
    private val dispatchers: DispatcherProvider,
) {
    suspend fun check(): Boolean = withContext(dispatchers.io) {
        runCatching {
            val request = Request.Builder()
                .url(STATUS_URL_PLACEHOLDER)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use false

                val body = response.body.string()
                if (body.isBlank()) return@use false

                JSONObject(body)
                    .optString("status")
                    .equals("UP", ignoreCase = true)
            }
        }.getOrDefault(false)
    }

    private companion object {
        private const val STATUS_URL_PLACEHOLDER = "https://placeholder/status"
    }
}
