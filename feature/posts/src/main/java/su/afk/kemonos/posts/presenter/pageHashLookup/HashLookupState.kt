package su.afk.kemonos.posts.presenter.pageHashLookup

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class HashLookupState {
    data class State(
        val hashInput: String = "",
        val selectedFileName: String? = null,
        val isLoading: Boolean = false,
        val isHashInvalid: Boolean = false,
        val errorMessage: String? = null,
        val result: HashLookupDomain? = null,
        val posts: Flow<PagingData<PostDomain>> = emptyFlow(),
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class HashChanged(val value: String) : Event
        data class FileHashDetected(val fileName: String?, val hash: String) : Event
        data object Submit : Event
        data class NavigateToPost(val post: PostDomain) : Event
        data object SwitchSite : Event
    }

    sealed interface Effect : UiEffect
}
