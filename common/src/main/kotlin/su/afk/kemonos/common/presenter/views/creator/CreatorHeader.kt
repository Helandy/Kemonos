package su.afk.kemonos.common.presenter.views.creator

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.views.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.util.getColorForFavorites
import su.afk.kemonos.common.util.selectDomain.getImageBaseUrlByService
import su.afk.kemonos.common.util.toUiDateTime

@Composable
fun CreatorHeader(
    service: String,
    creatorId: String,
    creatorName: String,
    updated: String?,
    showSearchButton: Boolean,
    showInfoButton: Boolean,
    onSearchClick: () -> Unit,
    onClickHeader: (() -> Unit?)?
) {
    val avatarSize = 42.dp
    var expanded by remember { mutableStateOf(false) }

    val imgBaseUrl = remember(service) {
        getImageBaseUrlByService(service)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val headerModifier = if (onClickHeader != null) {
            Modifier
                .weight(1f)
                .padding(end = 12.dp)
                .clickable { onClickHeader() }
        } else {
            Modifier
                .weight(1f)
                .padding(end = 12.dp)
        }

        /** Левая часть (аватар + инфо) занимает максимум места */
        Row(
            modifier = headerModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImageWithStatus(
                model = "$imgBaseUrl/icons/${service}/${creatorId}",
                contentDescription = creatorName,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            /** Отступ между аватаркой и текстом */
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = creatorName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
                /** Сервис */
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            1.dp,
                            getColorForFavorites(service),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = service,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = getColorForFavorites(service)
                    )
                }
            }
        }

        /** Правая часть: иконки */
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.info),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // todo сделать строительство ссылок на платформу юзера
                        //                    displayData?.let {
                        //                        DropdownMenuItem(
                        //                            text = {
                        //                                Text(
                        //                                    text = "🔗 ${it.service}",
                        //                                    color = MaterialTheme.colorScheme.primary
                        //                                )
                        //                            },
                        //                            onClick = {
                        //                                val intent = Intent(Intent.ACTION_VIEW, it.href.toUri())
                        //                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //                                context.startActivity(intent)
                        //                            }
                        //                        )
                        //                    }
                        updated?.let {
                            DropdownMenuItem(
                                text = {
                                    Text("📅 ${updated?.toUiDateTime()}")
                                },
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}