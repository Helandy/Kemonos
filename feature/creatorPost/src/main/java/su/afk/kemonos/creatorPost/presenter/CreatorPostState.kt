package su.afk.kemonos.creatorPost.presenter

import su.afk.kemonos.common.presenter.androidView.model.PostBlock
import su.afk.kemonos.common.presenter.baseViewModel.UiEffect
import su.afk.kemonos.common.presenter.baseViewModel.UiEvent
import su.afk.kemonos.common.presenter.baseViewModel.UiState
import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.creatorPost.domain.model.video.VideoThumbState
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.preferences.ui.UiSettingModel

internal class CreatorPostState {
    data class State(
        val loading: Boolean = false,

        val service: String = "",
        val id: String = "",
        val postId: String = "",

        val post: PostContentDomain? = null,
        val showButtonTranslate: Boolean = false,
        val contentBlocks: List<PostBlock>? = null,
        val commentDomains: List<CommentDomain> = emptyList(),

        val profile: Profile? = null,
        var showBarCreator: Boolean = false,

        val isFavoriteShowButton: Boolean = false,
        val isFavorite: Boolean = false,
        val favoriteActionLoading: Boolean = false,


        val translateExpanded: Boolean = false,
        val translateLoading: Boolean = false,
        val translateText: String? = null,
        val translateError: String? = null,

        val uiSettingModel: UiSettingModel = UiSettingModel(),

        val videoThumbs: Map<String, VideoThumbState> = emptyMap(),
        val videoInfo: Map<String, MediaInfoState> = emptyMap(),
        val audioInfo: Map<String, MediaInfoState> = emptyMap(),
    ) : UiState

    sealed interface Event : UiEvent {
        data object Retry : Event

        data object CopyPostLinkClicked : Event
        data object FavoriteClicked : Event

        data object CreatorHeaderClicked : Event

        data object ToggleTranslate : Event

        data class OpenImage(val originalUrl: String) : Event
        data class OpenExternalUrl(val url: String) : Event

        data class Download(val url: String, val fileName: String?) : Event

        data class VideoThumbRequested(val server: String, val path: String) : Event
        data class VideoInfoRequested(val server: String, val path: String) : Event
        data class AudioInfoRequested(val url: String) : Event

        data class PlayAudio(val url: String, val name: String?) : Event
    }

    sealed interface Effect : UiEffect {
        data class ShowToast(val message: String) : Effect
        data class CopyPostLink(val message: String) : Effect

        data class OpenGoogleTranslate(
            val text: String,
            val targetLangTag: String,
        ) : Effect

        data class DownloadToast(val fileName: String) : Effect

        data class OpenUrl(val url: String) : Effect

        data class OpenAudio(val url: String, val name: String?, val mime: String) : Effect
    }
}