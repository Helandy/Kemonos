package su.afk.kemonos.preferences.domainResolver

import androidx.compose.runtime.staticCompositionLocalOf

val LocalDomainResolver = staticCompositionLocalOf<IDomainResolver> {
    error("IDomainResolver is not provided")
}

/** Для превью */
object PreviewDomainResolver : IDomainResolver {
    override fun baseUrlByService(service: String): String {
        return "https://example.com"
    }

    override fun imageBaseUrlByService(service: String): String {
        return "https://img.example.com"
    }
}