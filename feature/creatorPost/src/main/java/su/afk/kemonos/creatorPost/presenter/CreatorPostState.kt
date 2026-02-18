package su.afk.kemonos.creatorPost.presenter

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.domain.model.media.MediaInfoState
import su.afk.kemonos.creatorPost.domain.model.video.VideoThumbState
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.ui.presenter.androidView.model.PostBlock
import su.afk.kemonos.ui.presenter.baseViewModel.UiEffect
import su.afk.kemonos.ui.presenter.baseViewModel.UiEvent
import su.afk.kemonos.ui.presenter.baseViewModel.UiState

internal class CreatorPostState {
    data class State(
        val loading: Boolean,

        val service: String,
        val id: String,
        val postId: String,

        val post: PostContentDomain?,
        val showButtonTranslate: Boolean,
        val contentBlocks: List<PostBlock>?,
        val commentDomains: List<CommentDomain>,

        val profile: Profile?,
        var showBarCreator: Boolean,

        val isFavoriteShowButton: Boolean,
        val isFavorite: Boolean,
        val favoriteActionLoading: Boolean,


        val translateExpanded: Boolean,
        val translateLoading: Boolean,
        val translateText: String?,
        val translateError: String?,

        val uiSettingModel: UiSettingModel,

        val videoThumbs: Map<String, VideoThumbState>,
        val videoInfo: Map<String, MediaInfoState>,
        val audioInfo: Map<String, MediaInfoState>,
    ) : UiState {
        companion object {
            fun default() = State(
                loading = true,
                service = "",
                id = "",
                postId = "",
                post = null,
                showButtonTranslate = false,
                contentBlocks = null,
                commentDomains = emptyList(),
                profile = null,
                showBarCreator = false,
                isFavoriteShowButton = false,
                isFavorite = false,
                favoriteActionLoading = false,
                translateExpanded = false,
                translateLoading = false,
                translateText = null,
                translateError = null,
                uiSettingModel = UiSettingModel(),
                videoThumbs = emptyMap(),
                videoInfo = emptyMap(),
                audioInfo = emptyMap(),
            )
        }
    }

    sealed interface Event : UiEvent {
        data object Retry : Event
        data object Back : Event

        data object CopyPostLinkClicked : Event
        data object FavoriteClicked : Event

        data object CreatorHeaderClicked : Event

        data object ToggleTranslate : Event

        data class OpenImage(val originalUrl: String) : Event

        data class Download(val url: String, val fileName: String?) : Event
        data object DownloadAllClicked : Event

        data class VideoThumbRequested(val server: String, val path: String) : Event
        data class VideoInfoRequested(val server: String, val path: String) : Event
        data class AudioInfoRequested(val url: String) : Event

        data class PlayAudio(val url: String, val name: String?) : Event

        data object OpenNextPost : Event
        data object OpenPrevPost : Event
    }

    sealed interface Effect : UiEffect {
        data class ShowToast(val message: String) : Effect
        data class CopyPostLink(val message: String) : Effect

        data class OpenGoogleTranslate(
            val text: String,
            val targetLangTag: String,
        ) : Effect

        data class DownloadToast(val fileName: String) : Effect

        data class OpenAudio(val url: String, val name: String?, val mime: String) : Effect
    }
}
