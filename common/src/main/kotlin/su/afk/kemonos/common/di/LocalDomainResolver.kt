package su.afk.kemonos.common.di

import androidx.compose.runtime.staticCompositionLocalOf
import su.afk.kemonos.preferences.IDomainResolver

val LocalDomainResolver = staticCompositionLocalOf<IDomainResolver> {
    error("IDomainResolver is not provided")
}

/** Для превью */
internal object PreviewDomainResolver : IDomainResolver {
    override fun baseUrlByService(service: String): String {
        return "https://example.com"
    }

    override fun imageBaseUrlByService(service: String): String {
        return "https://img.example.com"
    }
}