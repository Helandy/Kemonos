package su.afk.kemonos.preferences.domainResolver

import androidx.compose.runtime.staticCompositionLocalOf

val LocalDomainResolver = staticCompositionLocalOf<IDomainResolver> {
    error("IDomainResolver is not provided")
}

/** Для превью */
object PreviewDomainResolver : IDomainResolver {
    override fun selectedSite() = su.afk.kemonos.domain.SelectedSite.K

    override fun baseUrlByService(service: String): String {
        return "https://example.com"
    }

    override fun imageBaseUrlByService(service: String): String {
        return "https://img.example.com"
    }

    override fun creatorImageBaseUrlByService(service: String): String {
        return "https://example.com"
    }

    override fun pawchiveHostConfig() = PawchiveHostConfig(
        apiBaseUrl = "https://example.com/api/",
        rootUrl = "https://example.com",
        imageBaseUrl = "https://img.example.com",
        fileBaseUrl = "https://file.example.com",
    )
}
