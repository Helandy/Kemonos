package su.afk.kemonos.creatorProfile.presenter.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.creatorProfile.api.domain.models.profileSimilar.SimilarCreator
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.components.creator.ProfileLinkItem

@Composable
fun SimilarCreatorsScreen(
    dateMode: DateFormatMode,
    creators: List<SimilarCreator>,
    onClick: (SimilarCreator) -> Unit,
    modifier: Modifier = Modifier
) {
    if (creators.isEmpty()) return

    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(creators.first().service) {
        resolver.imageBaseUrlByService(creators.first().service)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(creators, key = { "${it.service}:${it.id}" }) { creator ->
            ProfileLinkItem(
                dateMode = dateMode,
                name = creator.name,
                service = creator.service,
                id = creator.id,
                updated = creator.updated,
                imgBaseUrl = imgBaseUrl,
                onClick = { onClick(creator) }
            )

            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                DividerDefaults.color
            )
        }
    }
}
