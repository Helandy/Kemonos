package su.afk.kemonos.creators.presenter.views

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
import su.afk.kemonos.common.presenter.views.imageLoader.AsyncImageWithStatus
import su.afk.kemonos.common.util.getColorForFavorites
import su.afk.kemonos.common.util.selectDomain.getImageBaseUrlByService
import su.afk.kemonos.core.utils.formatNumberWithSpaces
import su.afk.kemonos.domain.domain.models.Creators

@Composable
internal fun CreatorItem(creator: Creators, onClick: () -> Unit) {
    val avatarSize = LocalWindowInfo.current.containerSize.width * 0.15f

    val imgBaseUrl = remember(creator.service) {
        getImageBaseUrlByService(creator.service)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        /** Фоновое изображение (баннер) */
        AsyncImageWithStatus(
            model = "$imgBaseUrl/banners/${creator.service}/${creator.id}",
            contentDescription = "Banner for ${creator.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(shape = RoundedCornerShape(4.dp))
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
                model = "$imgBaseUrl/icons/${creator.service}/${creator.id}",
                contentDescription = creator.name,
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
                /** Имя креатора */
                Text(
                    text = creator.name,
                    maxLines = 1,
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
                            getColorForFavorites(creator.service),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column {
                        Text(
                            text = creator.service,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = getColorForFavorites(creator.service)
                        )

                        Text(
                            text = formatNumberWithSpaces(creator.favorited),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = getColorForFavorites(creator.service)
                        )
                    }
                }
            }
        }
    }
}
