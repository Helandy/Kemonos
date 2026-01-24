package su.afk.kemonos.creatorProfile.presenter.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.util.getColorForFavorites
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.preferences.ui.DateFormatMode

@Composable
fun ProfileLinksScreen(
    dateMode: DateFormatMode,
    links: List<ProfileLink>,
    onClick: (ProfileLink) -> Unit,
    modifier: Modifier = Modifier
) {
    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(links.first().service) { resolver.imageBaseUrlByService(links.first().service) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(links, key = { it.id }) { link ->
            ProfileLinkItem(
                dateMode = dateMode,
                link = link,
                imgBaseUrl = imgBaseUrl,
                onClick = onClick
            )

            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                DividerDefaults.color
            )
        }
    }
}

@Composable
fun ProfileLinkItem(
    dateMode: DateFormatMode,
    link: ProfileLink,
    imgBaseUrl: String,
    onClick: (ProfileLink) -> Unit
) {
    val avatarSize = LocalConfiguration.current.screenWidthDp * 0.18f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick(link) }
    ) {
        /** Фоновое изображение (баннер) */
        AsyncImageWithStatus(
            model = "$imgBaseUrl/banners/${link.service}/${link.id}",
            contentDescription = "Banner for ${link.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(4.dp))
        )

        /** Затемнение */
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
            /** Аватар */
            AsyncImageWithStatus(
                model = "$imgBaseUrl/icons/${link.service}/${link.id}",
                contentDescription = link.name,
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
                /** Имя */
                Text(
                    text = link.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                /** Сервис + дата обновления */
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
                                getColorForFavorites(link.service),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = link.service,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = getColorForFavorites(link.service)
                        )
                    }
                    link.updated?.let {
                        Text(
                            text = "📅 ${it.toUiDateTime(dateMode)}",
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