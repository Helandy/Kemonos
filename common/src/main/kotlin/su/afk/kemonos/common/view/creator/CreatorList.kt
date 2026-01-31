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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.common.utilsUI.formatNumberWithSpaces
import su.afk.kemonos.common.utilsUI.getColorForFavorites
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
internal fun CreatorListItem(
    dateMode: DateFormatMode,
    service: String,
    id: String,
    name: String,
    favorited: Int? = null,
    updated: String? = null,
    isFresh: Boolean = false,
    onClick: () -> Unit
) {
    val avatarSize = LocalWindowInfo.current.containerSize.width * 0.13f
    val avatarShape = RoundedCornerShape(10.dp)

    val accent = getColorForFavorites(service)
    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(service) { resolver.imageBaseUrlByService(service) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(110.dp)
            .clickable { onClick() }
    ) {
        /** Фоновое изображение (баннер) */
        AsyncImageWithStatus(
            model = "$imgBaseUrl/banners/${service}/${id}",
            contentDescription = "Banner for $name",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(4.dp))
                .matchParentSize()
        )

        /** Затемнение */
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape = RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /** Иконка (аватар) */
            AsyncImageWithStatus(
                model = "$imgBaseUrl/icons/${service}/${id}",
                contentDescription = name,
                modifier = Modifier
                    .size(avatarSize.dp)
                    .clip(avatarShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                /** Имя креатора */
                Text(
                    text = name,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                /** Чип с инфой */
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            2.dp,
                            accent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column {
                        Text(
                            text = service,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = accent
                        )

                        favorited?.let {
                            Text(
                                text = formatNumberWithSpaces(favorited),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = accent
                            )
                        }

                        updated?.let {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                    text = updated.toUiDateTime(dateMode),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = accent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
