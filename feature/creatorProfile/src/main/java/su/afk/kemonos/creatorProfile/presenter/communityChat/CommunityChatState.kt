package su.afk.kemonos.creatorProfile.presenter.communityChat

import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityMessage
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.CommunityMedia
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class CommunityChatState {

    data class State(
        val service: String = "",
        val creatorId: String = "",
        val channelId: String = "",
        val channelName: String = "",
        val channelPostCount: Int? = null,
        val reverseOrder: Boolean = false,
        val loading: Boolean = false,
        val loadingMore: Boolean = false,
        val canLoadMore: Boolean = false,
        val offset: Int = 0,
        val listFirstVisibleItemIndex: Int = 0,
        val listFirstVisibleItemScrollOffset: Int = 0,
        val messages: List<CommunityMessage> = emptyList(),
        val translateExpandedIds: Set<String> = emptySet(),
        val translateLoadingIds: Set<String> = emptySet(),
        val translatedTextById: Map<String, String> = emptyMap(),
        val translateErrorById: Map<String, String> = emptyMap(),
        val uiSettingModel: UiSettingModel = UiSettingModel(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event
        data object Back : Event
        data object CopyChatLink : Event
        data class ListScrollChanged(
            val firstVisibleItemIndex: Int,
            val firstVisibleItemScrollOffset: Int,
        ) : Event
        data object LoadMore : Event
        data object ToggleReverseOrder : Event
        data class OpenMedia(val media: CommunityMedia) : Event
        data class OpenUrl(val url: String) : Event
        data class ToggleTranslate(
            val messageId: String,
            val text: String,
        ) : Event
    }

    sealed interface Effect : UiEffect {
        data class OpenUrl(val url: String) : Effect
        data class CopyChatLink(val message: String) : Effect
        data class OpenVideo(
            val url: String,
            val fileName: String? = null,
        ) : Effect
        data class ShowToast(val message: String) : Effect
        data class DownloadToast(val fileName: String) : Effect
        data class OpenGoogleTranslate(
            val text: String,
            val targetLangTag: String,
        ) : Effect
    }
}
