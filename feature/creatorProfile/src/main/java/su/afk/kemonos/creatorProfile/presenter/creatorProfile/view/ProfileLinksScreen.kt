package su.afk.kemonos.creatorProfile.presenter.creatorProfile.view

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
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.ui.components.creator.ProfileLinkItem

@Composable
fun ProfileLinksScreen(
    dateMode: DateFormatMode,
    links: List<ProfileLink>,
    onClick: (ProfileLink) -> Unit,
    modifier: Modifier = Modifier
) {
    if (links.isEmpty()) return

    val resolver = LocalDomainResolver.current
    val imgBaseUrl = remember(links.first().service) { resolver.imageBaseUrlByService(links.first().service) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(links, key = { it.id }) { link ->
            ProfileLinkItem(
                dateMode = dateMode,
                name = link.name,
                service = link.service,
                id = link.id,
                updated = link.updated,
                imgBaseUrl = imgBaseUrl,
                onClick = { onClick(link) }
            )

            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                DividerDefaults.color
            )
        }
    }
}
