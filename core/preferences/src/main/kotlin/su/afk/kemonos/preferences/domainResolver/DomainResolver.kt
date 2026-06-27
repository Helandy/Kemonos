package su.afk.kemonos.preferences.domainResolver

import android.net.Uri
import androidx.core.net.toUri
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.GetCoomerRootUrlUseCase
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.preferences.GetPawchiveRootUrlUseCase
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.utils.pawchive.PawchiveConstants
import javax.inject.Inject

private val coomerServicesList = setOf("onlyfans", "fansly", "candfans")
private const val COOMER_REFERENCE_SERVICE = "onlyfans"

interface IDomainResolver {
    fun selectedSite(): SelectedSite
    fun baseUrlByService(service: String): String
    fun imageBaseUrlByService(service: String): String
    fun creatorImageBaseUrlByService(service: String): String = imageBaseUrlByService(service)
    fun fileBaseUrlByService(service: String): String = imageBaseUrlByService(service)
}

fun IDomainResolver.selectedSiteByService(service: String): SelectedSite {
    if (selectedSite() == SelectedSite.P) return SelectedSite.P
    return when (service) {
        in coomerServicesList -> SelectedSite.C
        else -> SelectedSite.K
    }
}

class DomainResolver @Inject constructor(
    private val getKemonoRootUrl: GetKemonoRootUrlUseCase,
    private val getCoomerRootUrl: GetCoomerRootUrlUseCase,
    private val getPawchiveRootUrl: GetPawchiveRootUrlUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) : IDomainResolver {

    override fun selectedSite(): SelectedSite = selectedSiteUseCase.getSite()

    override fun baseUrlByService(service: String): String {
        if (selectedSite() == SelectedSite.P) return getPawchiveRootUrl()
        return when (service) {
            in coomerServicesList -> getCoomerRootUrl()
            else -> getKemonoRootUrl()
        }
    }

    override fun imageBaseUrlByService(service: String): String {
        if (selectedSite() == SelectedSite.P) return "https://img.pawchive.st"
        val base = baseUrlByService(service)
        return base.toImgBaseUrl()
    }

    override fun creatorImageBaseUrlByService(service: String): String {
        if (selectedSite() == SelectedSite.P) return baseUrlByService(service)
        return imageBaseUrlByService(service)
    }

    override fun fileBaseUrlByService(service: String): String {
        if (selectedSite() == SelectedSite.P) return PawchiveConstants.FILE_BASE_URL
        return imageBaseUrlByService(service)
    }
}

/** "https://kemono.cr" -> "https://img.kemono.cr" */
private fun String.toImgBaseUrl(): String {
    return runCatching {
        val uri = toUri()
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
