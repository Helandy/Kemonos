package su.afk.kemonos.ui.components.posts.postCard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.ui.components.posts.postCard.model.PostCardMeta
import su.afk.kemonos.ui.components.posts.postCard.model.PreviewState
import su.afk.kemonos.ui.uiUtils.format.*

@Composable
internal fun rememberPostCardMeta(
    post: PostDomain,
    allowVideoPreview: Boolean = true,
): PostCardMeta {
    return remember(
        post.id,
        post.file?.path,
        post.attachments,
        post.favCount,
        allowVideoPreview,
    ) {
        val imagePath = findFirstImagePath(post)

        /**
         * Источник может отдавать готовую миниатюру для любого вложения, включая видео.
         * Признак — заполненный thumbnailPath, отдельного флага не нужно.
         */
        val selfThumbnail = post.attachments.firstOrNull { it.thumbnailPath != null }

        val preview = when {
            imagePath != null -> PreviewState.Image(
                path = imagePath,
                thumbnailPath = post.attachments
                    .firstOrNull { it.path == imagePath }
                    ?.thumbnailPath,
            )

            /** Видео без картинки: показываем миниатюру источника вместо заглушки. */
            selfThumbnail != null -> PreviewState.Image(
                path = selfThumbnail.path,
                thumbnailPath = selfThumbnail.thumbnailPath,
            )

            allowVideoPreview && (isVideoFile(post.file?.path) || post.attachments.any { isVideoFile(it.path) }) ->
                PreviewState.Video(path = findFirstVideoPath(post))

            isAudioFile(post.file?.path) || post.attachments.any { isAudioFile(it.path) } ->
                PreviewState.Audio

            else -> PreviewState.Empty
        }

        PostCardMeta(
            preview = preview,
            favCount = post.favCount ?: 0,
            videoCount = countVideoFiles(post)
        )
    }
}
