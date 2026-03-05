package su.afk.kemonos.creatorPost.presenter.model

import su.afk.kemonos.creatorPost.api.domain.model.CommentDomain
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.ui.presenter.androidView.model.PostBlock

internal data class LoadRequest(
    val requestId: Long,
    val service: String,
    val creatorId: String,
    val postId: String,
    val showComments: Boolean,
)

internal data class LoadedPostData(
    val sourcePost: PostContentDomain?,
    val resolvedPost: PostContentDomain?,
    val comments: List<CommentDomain>,
    val profile: Profile?,
    val selectedRevisionId: Int?,
    val revisionIds: List<Int?>,
    val showButtonTranslate: Boolean,
    val contentBlocks: List<PostBlock>,
)

internal data class LoadedRevisionData(
    val resolvedPost: PostContentDomain,
    val showButtonTranslate: Boolean,
    val contentBlocks: List<PostBlock>,
)
