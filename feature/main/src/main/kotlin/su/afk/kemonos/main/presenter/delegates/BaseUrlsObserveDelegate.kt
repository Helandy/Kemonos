package su.afk.kemonos.main.presenter.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import su.afk.kemonos.preferences.siteUrl.IGetBaseUrlsUseCase
import javax.inject.Inject

internal class BaseUrlsObserveDelegate @Inject constructor(
    private val getBaseUrlsUseCase: IGetBaseUrlsUseCase,
) {
    fun observe(scope: CoroutineScope, onUrls: (kemono: String, coomer: String) -> Unit) {
        scope.launch {
            combine(
                getBaseUrlsUseCase.kemonoUrl,
                getBaseUrlsUseCase.coomerUrl,
            ) { kemono, coomer -> kemono to coomer }
                .collect { (k, c) -> onUrls(k, c) }
        }
    }
}
