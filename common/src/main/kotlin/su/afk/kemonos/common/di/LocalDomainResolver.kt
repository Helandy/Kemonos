package su.afk.kemonos.common.di

import androidx.compose.runtime.staticCompositionLocalOf
import su.afk.kemonos.preferences.IDomainResolver

val LocalDomainResolver = staticCompositionLocalOf<IDomainResolver> {
    error("IDomainResolver is not provided")
}