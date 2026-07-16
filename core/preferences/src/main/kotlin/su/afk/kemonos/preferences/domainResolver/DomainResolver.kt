package su.afk.kemonos.preferences.domainResolver

import android.net.Uri
import androidx.core.net.toUri
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.GetCoomerRootUrlUseCase
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.preferences.GetPawchiveRootUrlUseCase
import su.afk.kemonos.preferences.UrlPrefs
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

private val coomerServicesList = setOf("onlyfans", "fansly", "candfans")
private const val COOMER_REFERENCE_SERVICE = "onlyfans"

interface IDomainResolver {
    fun selectedSite(): SelectedSite
    fun baseUrlByService(service: String): String
    fun imageBaseUrlByService(service: String): String
    fun creatorImageBaseUrlByService(service: String): String = imageBaseUrlByService(service)
    fun fileBaseUrlByService(service: String): String = imageBaseUrlByService(service)
    fun pawchiveHostConfig(): PawchiveHostConfig
}

data class PawchiveHostConfig(
    val apiBaseUrl: String,
    val rootUrl: String,
    val imageBaseUrl: String,
    val fileBaseUrl: String,
)

object PawchiveHostConfigResolver {
    fun resolve(
        apiBaseUrl: String,
        imageHostOverride: String,
        fileHostOverride: String,
    ): PawchiveHostConfig {
        val rootUrl = apiBaseUrl.toOriginUrl()
        return PawchiveHostConfig(
            apiBaseUrl = apiBaseUrl,
            rootUrl = rootUrl,
            imageBaseUrl = imageHostOverride.ifBlank { rootUrl.withHostPrefix("img") },
            fileBaseUrl = fileHostOverride.ifBlank { rootUrl.withHostPrefix("file") },
        )
    }
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
    private val urlPrefs: UrlPrefs,
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
        if (selectedSite() == SelectedSite.P) return pawchiveHostConfig().imageBaseUrl
        val base = baseUrlByService(service)
        return base.toImgBaseUrl()
    }

    override fun creatorImageBaseUrlByService(service: String): String {
        if (selectedSite() == SelectedSite.P) return pawchiveHostConfig().rootUrl
        return imageBaseUrlByService(service)
    }

    override fun fileBaseUrlByService(service: String): String {
        if (selectedSite() == SelectedSite.P) return pawchiveHostConfig().fileBaseUrl
        return imageBaseUrlByService(service)
    }

    override fun pawchiveHostConfig(): PawchiveHostConfig {
        return PawchiveHostConfigResolver.resolve(
            apiBaseUrl = urlPrefs.pawchiveUrl.value,
            imageHostOverride = urlPrefs.pawchiveImageHostOverride.value,
            fileHostOverride = urlPrefs.pawchiveFileHostOverride.value,
        )
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

private fun String.withHostPrefix(prefix: String): String {
    return runCatching {
        val uri = toUri()
        val scheme = uri.scheme ?: "https"
        val host = uri.host ?: return this
        val derivedHost = if (host.startsWith("$prefix.")) host else "$prefix.$host"
        Uri.Builder()
            .scheme(scheme)
            .encodedAuthority(derivedHost)
            .build()
            .toString()
    }.getOrElse { this }
}

private fun String.toOriginUrl(): String {
    return runCatching {
        val uri = toUri()
        val scheme = uri.scheme ?: "https"
        val authority = uri.encodedAuthority ?: return trimEnd('/')
        Uri.Builder()
            .scheme(scheme)
            .encodedAuthority(authority)
            .build()
            .toString()
    }.getOrElse { trimEnd('/').substringBefore("/api") }
}
