package su.afk.kemonos.common.view.posts.postCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
fun PostCard(
    post: PostDomain,
    onClick: () -> Unit,
    showFavCount: Boolean = false,
    dateMode: DateFormatMode,
    blurImage: Boolean,
) {
    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(post.service) { resolver.imageBaseUrlByService(post.service) }
    val meta = rememberPostCardMeta(post)
    val shape = RoundedCornerShape(4.dp)

    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp,
            hoveredElevation = 8.dp,
            focusedElevation = 8.dp,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            PostPreview(
                preview = meta.preview,
                imgBaseUrl = imgBaseUrl,
                title = post.title,
                textPreview = post.substring,
                blurImage = blurImage,
            )

            if (showFavCount && meta.favCount > 0) {
                CornerBadge(
                    text = "❤ ${meta.favCount}",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                )
            }

            if (meta.videoCount > 0) {
                CornerBadge(
                    text = "🎬 ${meta.videoCount}",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                )
            }
        }

        Column(
            modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
        ) {
            /** Тайтл и дата */
            if (showFavCount || post.title?.isNotBlank() == true) {
                Text(
                    text = post.title.orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row {
                Text(
                    text = "📅 ${post.published?.toUiDateTime(dateMode)} ",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (post.attachments.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.attachments_count, post.attachments.size),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview("PreviewPostCard")
@Composable
private fun PreviewPostCard() {
    KemonosPreviewScreen {
        PostCard(
            post = PostDomain.default(),
            onClick = {},
            showFavCount = false,
            dateMode = DateFormatMode.DD_MM_YYYY,
            blurImage = false,
        )
    }
}