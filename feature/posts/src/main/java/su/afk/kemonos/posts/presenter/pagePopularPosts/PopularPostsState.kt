package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.api.popular.PopularInfo
import su.afk.kemonos.posts.api.popular.PopularProps
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.preferences.ui.UiSettingModel

internal data class PopularPostsState(
    /** Популярные посты */
    val posts: Flow<PagingData<PostDomain>> = emptyFlow(),

    val popularPostsInfo: PopularInfo? = null,
    val popularProps: PopularProps? = null,

    val popularPeriod: Period = Period.RECENT,
    val popularDateForPopular: String? = null,

    val uiSettingModel: UiSettingModel = UiSettingModel(),
)