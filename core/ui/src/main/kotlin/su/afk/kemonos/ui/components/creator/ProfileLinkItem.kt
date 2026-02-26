package su.afk.kemonos.ui.components.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.ui.uiUtils.color.getColorForFavorites

@Composable
fun ProfileLinkItem(
    dateMode: DateFormatMode,
    name: String,
    service: String,
    id: String,
    updated: String?,
    imgBaseUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarSize = LocalConfiguration.current.screenWidthDp * 0.18f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImageWithStatus(
            model = "$imgBaseUrl/banners/$service/$id",
            contentDescription = "Banner for $name",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(4.dp))
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImageWithStatus(
                model = "$imgBaseUrl/icons/$service/$id",
                contentDescription = name,
                modifier = Modifier
                    .size(avatarSize.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                2.dp,
                                getColorForFavorites(service),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = service,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = getColorForFavorites(service)
                        )
                    }
                    updated?.let {
                        Text(
                            text = "\uD83D\uDCC5 ${it.toUiDateTime(dateMode)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
