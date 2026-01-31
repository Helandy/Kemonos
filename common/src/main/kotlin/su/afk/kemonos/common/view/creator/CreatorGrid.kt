package su.afk.kemonos.common.view.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.common.utilsUI.formatNumberWithSpaces
import su.afk.kemonos.common.utilsUI.getColorForFavorites
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
internal fun CreatorGridItem(
    dateMode: DateFormatMode,
    service: String,
    id: String,
    name: String,
    favorited: Int? = null,
    updated: String? = null,
    isFresh: Boolean = false,
    onClick: () -> Unit,
) {
    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(service) { resolver.imageBaseUrlByService(service) }

    val accent = getColorForFavorites(service)
    val shape = RoundedCornerShape(4.dp)
    val avatarShape = RoundedCornerShape(10.dp)
    val chipShape = RoundedCornerShape(10.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(shape)
            .clickable { onClick() }
    ) {
        AsyncImageWithStatus(
            model = "$imgBaseUrl/banners/${service}/${id}",
            contentDescription = "Banner for $name",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImageWithStatus(
                    model = "$imgBaseUrl/icons/${service}/${id}",
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(avatarShape)
                        .border(1.dp, accent, avatarShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.20f))
                )

                Spacer(Modifier.width(6.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.End),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(chipShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.78f))
                            .border(2.dp, accent.copy(alpha = 0.9f), chipShape)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = service,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = accent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (favorited != null) {
                                Text(
                                    text = formatNumberWithSpaces(favorited),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = accent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            updated?.let {
                                if (isFresh) {
                                    Text(
                                        text = "NEW",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                }

                                Text(
                                    text = it.toUiDateTime(dateMode),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = accent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            )
        }
    }
}

@Preview("previewCreatorGridItem")
@Composable
private fun PreviewCreatorGridItem() {
    KemonosPreviewScreen {
        CreatorGridItem(
            service = "patreon",
            id = "1",
            name = "Sandy Sandy Sandy Sandy Sandy",
            favorited = 123456,
            onClick = {},
            dateMode = DateFormatMode.DD_MM_YYYY,
        )
    }
}