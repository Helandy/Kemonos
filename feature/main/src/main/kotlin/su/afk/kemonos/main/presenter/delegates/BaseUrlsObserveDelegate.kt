package su.afk.kemonos.main.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.siteUrl.IGetBaseUrlsUseCase
import javax.inject.Inject

internal class BaseUrlsObserveDelegate @Inject constructor(
    private val getBaseUrlsUseCase: IGetBaseUrlsUseCase,
) {
    fun observe(scope: CoroutineScope, onUrls: (Map<SelectedSite, String>) -> Unit) {
        scope.launch {
            val sites = SelectedSite.entries
            combine(sites.map(getBaseUrlsUseCase::siteUrl)) { urls ->
                sites.zip(urls.toList()).toMap()
            }.collect(onUrls)
        }
    }
}
