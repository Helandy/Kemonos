package su.afk.kemonos.preferences

import android.net.Uri
import javax.inject.Inject

private val coomerServicesList = setOf("onlyfans", "fansly", "candfans")

interface IDomainResolver {
    fun baseUrlByService(service: String): String
    fun imageBaseUrlByService(service: String): String
}

class DomainResolver @Inject constructor(
    private val getKemonoRootUrl: GetKemonoRootUrlUseCase,
    private val getCoomerRootUrl: GetCoomerRootUrlUseCase,
) : IDomainResolver {

    override fun baseUrlByService(service: String): String {
        return if (service in coomerServicesList) getCoomerRootUrl()
        else getKemonoRootUrl()
    }

    override fun imageBaseUrlByService(service: String): String {
        val base = baseUrlByService(service)
        return base.toImgBaseUrl()
    }
}

/** "https://kemono.cr" -> "https://img.kemono.cr" */
private fun String.toImgBaseUrl(): String {
    return runCatching {
        val uri = Uri.parse(this)
        val scheme = uri.scheme ?: "https"
        val host = uri.host ?: return this

        /** если уже img.* — не трогаем */
        val imgHost = if (host.startsWith("img.")) host else "img.$host"
        Uri.Builder()
            .scheme(scheme)
            .encodedAuthority(imgHost)
            .build()
            .toString()
    }.getOrElse { this }
}