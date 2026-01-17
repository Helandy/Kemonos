package su.afk.kemonos.creatorPost.presenter

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.preferences.ui.UiSettingModel

internal data class CreatorPostState(
    val loading: Boolean = false,

    val service: String = "",
    val id: String = "",
    val postId: String = "",

    val post: PostContentDomain? = null,
    val postContentClean: String = "",
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
)

sealed interface CreatorPostEffect {
    data class ShowToast(val message: String) : CreatorPostEffect
    data class CopyPostLink(val message: String) : CreatorPostEffect

    data class OpenGoogleTranslate(
        val text: String,
        val targetLangTag: String,
    ) : CreatorPostEffect
}