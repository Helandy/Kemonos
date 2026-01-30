package su.afk.kemonos.common.view.posts.postCard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import su.afk.kemonos.common.util.*
import su.afk.kemonos.common.view.posts.postCard.model.PostCardMeta
import su.afk.kemonos.common.view.posts.postCard.model.PreviewState
import su.afk.kemonos.domain.models.PostDomain

@Composable
internal fun rememberPostCardMeta(post: PostDomain): PostCardMeta {
    return remember(
        post.id,
        post.file?.path,
        post.attachments,
        post.favCount
    ) {
        val imagePath = findFirstImagePath(post)

        val preview = when {
            imagePath != null -> PreviewState.Image(imagePath)

            isVideoFile(post.file?.path) || post.attachments.any { isVideoFile(it.path) } ->
                PreviewState.Video(url = findFirstVideoPath(post))

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