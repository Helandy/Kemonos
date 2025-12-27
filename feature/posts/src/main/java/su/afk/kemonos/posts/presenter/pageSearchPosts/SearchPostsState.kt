package su.afk.kemonos.posts.presenter.pageSearchPosts

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.domain.models.PostDomain

internal data class SearchPostsState(
    /** Поиск постов */
    val searchQuery: String = "",
    val posts: Flow<PagingData<PostDomain>> = emptyFlow(),
)