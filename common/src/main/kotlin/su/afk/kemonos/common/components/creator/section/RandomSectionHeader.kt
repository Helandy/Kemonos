package su.afk.kemonos.common.components.creator.section

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.components.creator.CreatorGridItem
import su.afk.kemonos.common.components.creator.CreatorListItem
import su.afk.kemonos.domain.models.creator.FavoriteArtist
import su.afk.kemonos.preferences.ui.DateFormatMode

internal fun LazyListScope.randomCreatorsSection(
    dateMode: DateFormatMode,
    items: List<FavoriteArtist>,
    onCreatorClick: (FavoriteArtist) -> Unit,
    expanded: Boolean?,
    onClickRandomHeader: (() -> Unit)? = null,
) {
    if (items.isEmpty()) return

    item(key = "random_title") {
        CreatorsSectionHeader(
            title = stringResource(R.string.random_creators_title),
            topSpace = 0.dp,
            showTopDivider = false,
            showBottomDivider = true,
            expanded = expanded,
            onClick = onClickRandomHeader
        )
    }

    expanded?.let { if (!it) return }

    items(
        count = items.size,
        key = { index ->
            val c = items[index]
            "rand:${c.service}:${c.id}:${c.indexed}"
        }
    ) { index ->
        val creator = items[index]
        CreatorListItem(
            dateMode = dateMode,
            service = creator.service,
            id = creator.id,
            name = creator.name,
            favorited = creator.favorited,
            onClick = { onCreatorClick(creator) }
        )
    }
}

internal fun LazyGridScope.randomCreatorsSection(
    dateMode: DateFormatMode,
    items: List<FavoriteArtist>,
    onCreatorClick: (FavoriteArtist) -> Unit,
    expanded: Boolean?,
    onClickRandomHeader: (() -> Unit)? = null,
) {
    if (items.isEmpty()) return

    item(
        key = "random_title",
        span = { GridItemSpan(maxLineSpan) }
    ) {
        CreatorsSectionHeader(
            title = stringResource(R.string.random_creators_title),
            topSpace = 0.dp,
            showTopDivider = false,
            showBottomDivider = true,
            expanded = expanded,
            onClick = onClickRandomHeader
        )
    }

    expanded?.let { if (!it) return }

    items(
        count = items.size,
        key = { index ->
            val c = items[index]
            "rand:${c.service}:${c.id}:${c.indexed}"
        }
    ) { index ->
        val creator = items[index]
        CreatorGridItem(
            dateMode = dateMode,
            service = creator.service,
            id = creator.id,
            name = creator.name,
            favorited = creator.favorited,
            onClick = { onCreatorClick(creator) }
        )
    }
}