package su.afk.kemonos.posts.presenter.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.UiSettingModel

internal const val POSTS_SEARCH_DEBOUNCE_MILLIS = 1_500L

internal fun IUiSettingUseCase.observeDistinct(
    scope: CoroutineScope,
    onEachModel: (UiSettingModel) -> Unit,
) {
    prefs
        .distinctUntilChanged()
        .onEach(onEachModel)
        .launchIn(scope)
}
