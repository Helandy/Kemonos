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

/** Базовый адрес выбранного сейчас источника. */
class SwitchingBaseUrlProvider(
    scope: CoroutineScope,
    prefs: UrlPrefs,
) : FlowBaseUrlProvider(
    scope = scope,
    initialUrl = prefs.siteUrl(prefs.selectedSite.value).value,
    urlFlow = selectedSiteUrlFlow(prefs),
)

/** Не растёт при добавлении источника. */
private fun selectedSiteUrlFlow(prefs: UrlPrefs): Flow<String> {
    val sites = SelectedSite.entries
    val urlsBySite = combine(sites.map(prefs::siteUrl)) { urls ->
        sites.zip(urls.toList()).toMap()
    }
    return combine(prefs.selectedSite, urlsBySite) { site, bySite -> bySite.getValue(site) }
        .distinctUntilChanged()
}
