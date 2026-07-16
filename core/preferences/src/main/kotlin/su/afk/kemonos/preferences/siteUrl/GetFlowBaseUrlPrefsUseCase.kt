package su.afk.kemonos.preferences.siteUrl

import kotlinx.coroutines.flow.StateFlow
import su.afk.kemonos.preferences.UrlPrefs
import javax.inject.Inject

internal class GetFlowBaseUrlPrefsUseCase @Inject constructor(
    private val urlPrefs: UrlPrefs,
) : IGetBaseUrlsUseCase {

    override val kemonoUrl: StateFlow<String> = urlPrefs.kemonoUrl
    override val coomerUrl: StateFlow<String> = urlPrefs.coomerUrl
    override val pawchiveUrl: StateFlow<String> = urlPrefs.pawchiveUrl
    override val pawchiveImageHostOverride: StateFlow<String> = urlPrefs.pawchiveImageHostOverride
    override val pawchiveFileHostOverride: StateFlow<String> = urlPrefs.pawchiveFileHostOverride
}
