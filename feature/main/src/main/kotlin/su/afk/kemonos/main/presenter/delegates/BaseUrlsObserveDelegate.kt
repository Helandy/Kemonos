package su.afk.kemonos.main.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import su.afk.kemonos.preferences.siteUrl.IGetBaseUrlsUseCase
import javax.inject.Inject

internal class BaseUrlsObserveDelegate @Inject constructor(
    private val getBaseUrlsUseCase: IGetBaseUrlsUseCase,
) {
    fun observe(scope: CoroutineScope, onUrls: (kemono: String, coomer: String, pawchive: String) -> Unit) {
        scope.launch {
            combine(
                getBaseUrlsUseCase.kemonoUrl,
                getBaseUrlsUseCase.coomerUrl,
                getBaseUrlsUseCase.pawchiveUrl,
            ) { kemono, coomer, pawchive -> Triple(kemono, coomer, pawchive) }
                .collect { (k, c, p) -> onUrls(k, c, p) }
        }
    }
}
