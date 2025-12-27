package su.afk.kemonos.creators.presenter.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.views.searchBar.SortOption
import su.afk.kemonos.domain.domain.models.CreatorsSort

@Composable
fun creatorsSortOptions(): List<SortOption<CreatorsSort>> = listOf(
    SortOption(CreatorsSort.POPULARITY, stringResource(R.string.sort_popularity)),
    SortOption(CreatorsSort.INDEX, stringResource(R.string.sort_index)),
    SortOption(CreatorsSort.UPDATE, stringResource(R.string.sort_update)),
    SortOption(CreatorsSort.NAME, stringResource(R.string.sort_name)),
)