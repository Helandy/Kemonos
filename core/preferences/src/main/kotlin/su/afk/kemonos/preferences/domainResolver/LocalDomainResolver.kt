package su.afk.kemonos.preferences.domainResolver

import androidx.compose.runtime.staticCompositionLocalOf
import su.afk.kemonos.domain.SelectedSite

val LocalDomainResolver = staticCompositionLocalOf<IDomainResolver> {
    error("IDomainResolver is not provided")
}

/** Для превью */
object PreviewDomainResolver : IDomainResolver {
    override fun selectedSite() = SelectedSite.K

    override fun baseUrlByService(service: String) = "https://example.com"

    override fun imageBaseUrlByService(service: String) = "https://img.example.com"

    override fun creatorImageBaseUrlByService(service: String) = "https://example.com"

    override fun fileBaseUrlByService(service: String) = "https://file.example.com"

    override fun hostConfig(site: SelectedSite) = SiteHostConfig(
        apiBaseUrl = "https://example.com/api/",
        rootUrl = "https://example.com",
        imageBaseUrl = "https://img.example.com",
        fileBaseUrl = "https://file.example.com",
        creatorImageBaseUrl = "https://example.com",
    )
}
