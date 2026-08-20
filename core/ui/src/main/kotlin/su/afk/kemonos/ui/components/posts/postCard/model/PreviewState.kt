package su.afk.kemonos.ui.components.posts.postCard.model

internal sealed interface PreviewState {
    /** [thumbnailPath] — готовый путь миниатюры, если источник задаёт его явно. */
    data class Image(val path: String, val thumbnailPath: String? = null) : PreviewState
    data class Video(val path: String? = null) : PreviewState
    data object Audio : PreviewState
    data object Empty : PreviewState
}

internal data class PostCardMeta(
    val preview: PreviewState,
    val favCount: Int,
    val videoCount: Int,
)
