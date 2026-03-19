package su.afk.kemonos.network.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.UrlPrefs
import java.util.concurrent.atomic.AtomicReference

interface BaseUrlProvider {
    fun get(): HttpUrl
}

open class FlowBaseUrlProvider(
    scope: CoroutineScope,
    initialUrl: String,
    urlFlow: Flow<String>,
) : BaseUrlProvider {

    private val ref = AtomicReference(initialUrl.toHttpUrl())

    init {
        scope.launch {
            urlFlow.collect { url -> ref.set(url.toHttpUrl()) }
        }
    }

    override fun get(): HttpUrl = ref.get()
}

/** Сменай baseUrl сайта  */
class SwitchingBaseUrlProvider(
    scope: CoroutineScope,
    prefs: UrlPrefs
) : FlowBaseUrlProvider(
    scope = scope,
    initialUrl = when (prefs.selectedSite.value) {
        SelectedSite.K -> prefs.kemonoUrl.value
        SelectedSite.C -> prefs.coomerUrl.value
    },
    urlFlow = combine(
        prefs.selectedSite,
        prefs.kemonoUrl,
        prefs.coomerUrl
    ) { site, kUrl, cUrl ->
        when (site) {
            SelectedSite.K -> kUrl
            SelectedSite.C -> cUrl
        }
    }.distinctUntilChanged(),
)
