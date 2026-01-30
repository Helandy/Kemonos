package su.afk.kemonos.common.view.posts.postCard.model

internal sealed interface PreviewState {
    data class Image(val path: String) : PreviewState
    data class Video(val url: String? = null) : PreviewState
    data object Audio : PreviewState
    data object Empty : PreviewState
}

internal data class PostCardMeta(
    val preview: PreviewState,
    val favCount: Int,
    val videoCount: Int,
)