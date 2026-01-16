package su.afk.kemonos.common.presenter.postsScreen.postCard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.domain.models.PostDomain

@Composable
fun PostCard(
    post: PostDomain,
    onClick: () -> Unit,
    showFavCount: Boolean = false,
) {
    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(post.service) { resolver.imageBaseUrlByService(post.service) }
    val meta = rememberPostCardMeta(post)

    Column(modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            PostPreview(
                preview = meta.preview,
                imgBaseUrl = imgBaseUrl,
                title = post.title,
                modifier = Modifier.fillMaxSize()
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

        /** Тайтл и дата */
        if (!post.title.isNullOrEmpty()) {
            Text(
                text = post.title.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.padding(top = 4.dp))
        }

        Row {
            Text(
                text = "📅 ${post.published?.toUiDateTime()} ",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (post.attachments.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.attachments_count, post.attachments.size),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}