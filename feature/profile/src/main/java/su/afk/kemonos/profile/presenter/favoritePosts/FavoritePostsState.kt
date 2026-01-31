package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.preferences.ui.UiSettingModel

internal data class FavoritePostsState(
    val selectSite: SelectedSite = SelectedSite.K,
    val loading: Boolean = false,
    val uiSettingModel: UiSettingModel = UiSettingModel(),

    val searchQuery: String = "",
    val posts: Flow<PagingData<PostDomain>> = emptyFlow(),
)