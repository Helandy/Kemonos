package su.afk.kemonos.creatorProfile.presenter.communityChat.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorProfile.presenter.communityChat.model.CommunityMedia
import su.afk.kemonos.ui.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.ui.uiUtils.format.isImageFile
import su.afk.kemonos.ui.uiUtils.format.isVideoFile
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
internal fun MediaGrid(
    items: List<CommunityMedia>,
    autoplayVideoInline: Boolean,
    onOpenMedia: (CommunityMedia) -> Unit
) {
    val singleVisualItem = items.singleOrNull()?.let { media ->
        isImageFile(media.pathOrUrl) || isVideoFile(media.pathOrUrl)
    } == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowItems.forEach { media ->
                    val itemModifier = if (singleVisualItem) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.weight(1f)
                    }

                    Box(modifier = itemModifier) {
                        val isImage = isImageFile(media.pathOrUrl)
                        val isVideo = isVideoFile(media.pathOrUrl)
                        val videoLabel = if (isVideo) resolveVideoLabel(media) else null
                        val previewTypePath = media.previewUrl.substringBefore('#').substringBefore('?')
                        val hasImagePreview = isImageFile(previewTypePath)

                        if (isImage) {
                            AsyncImageWithStatus(
                                model = media.previewUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenMedia(media) },
                                contentScale = ContentScale.Crop
                            )
                        } else if (isVideo && autoplayVideoInline) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onOpenMedia(media) }
                            ) {
                                InlineVideoPreview(
                                    url = media.openUrl,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (!videoLabel.isNullOrBlank()) {
                                    Text(
                                        text = videoLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.82f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        } else if (isVideo && hasImagePreview) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onOpenMedia(media) },
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                AsyncImageWithStatus(
                                    model = media.previewUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = videoLabel ?: "Video",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.82f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onOpenMedia(media) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = media.fileName?.takeIf { it.isNotBlank() }
                                        ?: if (isVideoFile(media.pathOrUrl)) "Video" else "Open file",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                if (rowItems.size == 1 && !singleVisualItem) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private fun resolveVideoLabel(media: CommunityMedia): String? {
    val explicitName = media.fileName?.trim().orEmpty()
    if (explicitName.isNotBlank()) return explicitName

    val fromUrl = media.openUrl
        .substringAfterLast('/')
        .substringBefore('?')
        .substringBefore('#')
        .trim()
    if (fromUrl.isBlank()) return null

    return runCatching {
        URLDecoder.decode(fromUrl, StandardCharsets.UTF_8.name())
    }.getOrDefault(fromUrl)
}
