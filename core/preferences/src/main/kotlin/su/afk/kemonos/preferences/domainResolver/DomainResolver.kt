package su.afk.kemonos.preferences.domainResolver

import android.net.Uri
import androidx.core.net.toUri
import su.afk.kemonos.domain.CreatorImageHost
import su.afk.kemonos.domain.MediaUrlScheme
import su.afk.kemonos.domain.mediaUrlScheme
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.SiteCatalog
import su.afk.kemonos.domain.spec
import su.afk.kemonos.preferences.GetRootUrlUseCase
import su.afk.kemonos.preferences.UrlPrefs
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

/** Разрешённые адреса одного источника. */
data class SiteHostConfig(
    val apiBaseUrl: String,
    val rootUrl: String,
    val imageBaseUrl: String,
    val fileBaseUrl: String,
    val creatorImageBaseUrl: String,
)

interface IDomainResolver {
    fun selectedSite(): SelectedSite
    fun baseUrlByService(service: String): String
    fun imageBaseUrlByService(service: String): String
    fun creatorImageBaseUrlByService(service: String): String
    fun fileBaseUrlByService(service: String): String

    /** Полный набор адресов источника. */
    fun hostConfig(site: SelectedSite): SiteHostConfig
}

object SiteHostConfigResolver {
    fun resolve(
        site: SelectedSite,
        apiBaseUrl: String,
        imageHostOverride: String = "",
        fileHostOverride: String = "",
    ): SiteHostConfig {
        val hosts = site.spec.mediaHosts
        val rootUrl = apiBaseUrl.toOriginUrl()

        val imageBaseUrl = imageHostOverride.ifBlank {
            rootUrl.withHostPrefix(hosts.imageHostPrefix)
        }
        val fileBaseUrl = fileHostOverride.ifBlank {
            rootUrl.withHostPrefix(hosts.fileHostPrefix)
        }

        return SiteHostConfig(
            apiBaseUrl = apiBaseUrl,
            rootUrl = rootUrl,
            imageBaseUrl = imageBaseUrl,
            fileBaseUrl = fileBaseUrl,
            creatorImageBaseUrl = when (hosts.creatorImageHost) {
                CreatorImageHost.ROOT -> rootUrl
                CreatorImageHost.IMAGE -> imageBaseUrl
            },
        )
    }
}

/**
 * Какому источнику принадлежит контент этого сервиса.
 *
 * Самодостаточный источник обслуживает свои сервисы сам; для федеративной пары
 * Kemono/Coomer решает сам сервис.
 */
fun IDomainResolver.selectedSiteByService(service: String): SelectedSite {
    val current = selectedSite()
    return if (current.spec.standalone) current else SiteCatalog.siteByService(service)
}

/** Схема сборки ссылок на медиа у источника, которому принадлежит сервис. */
fun IDomainResolver.mediaUrlSchemeByService(service: String): MediaUrlScheme =
    selectedSiteByService(service).mediaUrlScheme

class DomainResolver @Inject constructor(
    private val getRootUrl: GetRootUrlUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val urlPrefs: UrlPrefs,
) : IDomainResolver {

    override fun selectedSite(): SelectedSite = selectedSiteUseCase.getSite()

    override fun baseUrlByService(service: String): String =
        getRootUrl(selectedSiteByService(service))

    override fun imageBaseUrlByService(service: String): String =
        hostConfigByService(service).imageBaseUrl

    override fun creatorImageBaseUrlByService(service: String): String =
        hostConfigByService(service).creatorImageBaseUrl

    override fun fileBaseUrlByService(service: String): String =
        hostConfigByService(service).fileBaseUrl

    override fun hostConfig(site: SelectedSite): SiteHostConfig =
        SiteHostConfigResolver.resolve(
            site = site,
            apiBaseUrl = urlPrefs.siteUrl(site).value,
            imageHostOverride = urlPrefs.imageHostOverride(site).value,
            fileHostOverride = urlPrefs.fileHostOverride(site).value,
        )

    private fun hostConfigByService(service: String): SiteHostConfig =
        hostConfig(selectedSiteByService(service))
}

/** "https://kemono.cr" + "img" -> "https://img.kemono.cr". Префикс null оставляет хост как есть. */
private fun String.withHostPrefix(prefix: String?): String {
    if (prefix == null) return this
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
