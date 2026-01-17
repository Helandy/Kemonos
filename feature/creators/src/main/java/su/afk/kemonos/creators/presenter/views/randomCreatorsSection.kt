package su.afk.kemonos.creators.presenter.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.views.creator.grid.CreatorGridItem
import su.afk.kemonos.common.presenter.views.creator.list.CreatorListItem
import su.afk.kemonos.domain.models.Creators

internal fun LazyListScope.randomCreatorsSection(
    items: List<Creators>,
    onCreatorClick: (Creators) -> Unit,
) {
    if (items.isEmpty()) return

    item(key = "random_title") {
        Text(
            text = stringResource(R.string.random_creators_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillParentMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }

    items(
        count = items.size,
        key = { index ->
            val c = items[index]
            "rand:${c.service}:${c.id}:${c.indexed}"
        }
    ) { index ->
        val creator = items[index]
        CreatorListItem(
            service = creator.service,
            id = creator.id,
            name = creator.name,
            favorited = creator.favorited,
            onClick = { onCreatorClick(creator) }
        )
        HorizontalDivider()
    }

    item(key = "random_divider") {
        HorizontalDivider(
            Modifier.padding(top = 4.dp),
            DividerDefaults.Thickness,
            DividerDefaults.color
        )
    }
}

internal fun LazyGridScope.randomCreatorsSection(
    items: List<Creators>,
    onCreatorClick: (Creators) -> Unit,
) {
    if (items.isEmpty()) return

    item(key = "random_title", span = { GridItemSpan(maxLineSpan) }) {
        Text(
            text = stringResource(R.string.random_creators_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
    }

    items(
        count = items.size,
        key = { index ->
            val c = items[index]
            "rand:${c.service}:${c.id}:${c.indexed}"
        }
    ) { index ->
        val creator = items[index]
        CreatorGridItem(
            service = creator.service,
            id = creator.id,
            name = creator.name,
            favorited = creator.favorited,
            onClick = { onCreatorClick(creator) }
        )
    }

    item(key = "random_divider", span = { GridItemSpan(maxLineSpan) }) {
        HorizontalDivider(
            Modifier.padding(top = 4.dp),
            DividerDefaults.Thickness,
            DividerDefaults.color
        )
    }
}