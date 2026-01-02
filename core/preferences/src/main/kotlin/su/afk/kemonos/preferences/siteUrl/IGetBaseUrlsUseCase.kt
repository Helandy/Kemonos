package su.afk.kemonos.preferences.siteUrl

import kotlinx.coroutines.flow.StateFlow

interface IGetBaseUrlsUseCase {
    val kemonoUrl: StateFlow<String>
    val coomerUrl: StateFlow<String>
}