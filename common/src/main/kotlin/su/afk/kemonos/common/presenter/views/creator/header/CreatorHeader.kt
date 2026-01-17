package su.afk.kemonos.common.presenter.views.creator.header

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.util.getColorForFavorites
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.common.utilsUI.KemonoPreviewScreen

@Composable
fun CreatorHeader(
    service: String,
    creatorId: String,
    creatorName: String,
    updated: String?,
    showSearchButton: Boolean,
    showInfoButton: Boolean,
    onSearchClick: () -> Unit,
    onClickHeader: (() -> Unit)?,
) {
    val shape = RoundedCornerShape(12.dp)
    val avatarSize = 54.dp
    var expanded by remember { mutableStateOf(false) }

    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(service) { resolver.imageBaseUrlByService(service) }
    val accent = getColorForFavorites(service)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
    ) {
        // 1) Фон: баннер
        AsyncImageWithStatus(
            model = "$imgBaseUrl/banners/${service}/${creatorId}",
            contentDescription = "Banner for $creatorName",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // 2) Затемнение (чтобы текст читался)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.40f))
        )

        // 3) Контент
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val headerModifier = Modifier
                .weight(1f)
                .then(if (onClickHeader != null) Modifier.clickable { onClickHeader() } else Modifier)
                .padding(end = 12.dp)

            Row(
                modifier = headerModifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImageWithStatus(
                    model = "$imgBaseUrl/icons/${service}/${creatorId}",
                    contentDescription = creatorName,
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, accent, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = creatorName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(1.dp, accent, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = service,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = accent
                        )
                    }
                }
            }

            if (showInfoButton) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showSearchButton) {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search),
                                tint = Color.White
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.info),
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            updated?.let { upd ->
                                DropdownMenuItem(
                                    text = { Text("📅 ${upd.toUiDateTime()}") },
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview("PreviewCreatorHeader")
@Composable
private fun PreviewCreatorHeader() {
    KemonoPreviewScreen {
        CreatorHeader(
            service = "creator",
            creatorId = "creator",
            creatorName = "creator",
            updated = null,
            showSearchButton = true,
            showInfoButton = true,
            onSearchClick = {},
            onClickHeader = {},
        )
    }
}