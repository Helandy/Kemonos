package su.afk.kemonos.storage.di

import dagger.MapKey
import su.afk.kemonos.domain.SelectedSite

/** Ключ multibinding-мапы «источник -> DAO». */
@MapKey
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SiteKey(val site: SelectedSite)
