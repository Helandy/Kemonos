package su.afk.kemonos.posts.presenter.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.api.repository.blacklist.blacklistKey

internal const val POSTS_SEARCH_DEBOUNCE_MILLIS = 700L

internal fun IStoreBlacklistedAuthorsRepository.observeBlacklistedAuthorKeys(): Flow<Set<String>> =
    observeAll()
        .map { items ->
            items.mapTo(mutableSetOf()) { blacklisted ->
                blacklistKey(blacklisted.service, blacklisted.creatorId)
            }
        }
        .distinctUntilChanged()

internal fun IUiSettingUseCase.observeDistinct(
    scope: CoroutineScope,
    onEachModel: (UiSettingModel) -> Unit,
) {
    prefs
        .distinctUntilChanged()
        .onEach(onEachModel)
        .launchIn(scope)
}
