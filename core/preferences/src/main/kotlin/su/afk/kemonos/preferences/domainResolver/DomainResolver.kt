package su.afk.kemonos.preferences.domainResolver

import android.net.Uri
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.GetCoomerRootUrlUseCase
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import javax.inject.Inject

private val coomerServicesList = setOf("onlyfans", "fansly", "candfans")
private const val COOMER_REFERENCE_SERVICE = "onlyfans"

interface IDomainResolver {
    fun baseUrlByService(service: String): String
    fun imageBaseUrlByService(service: String): String
}

fun IDomainResolver.selectedSiteByService(service: String): SelectedSite {
    val targetBase = baseUrlByService(service)
    val coomerBase = baseUrlByService(COOMER_REFERENCE_SERVICE)
    return if (targetBase == coomerBase) SelectedSite.C else SelectedSite.K
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
