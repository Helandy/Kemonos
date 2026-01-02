package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.models.PostDomain

internal data class TagsSelectState(
    val loading: Boolean = false,

    /** Выбранный тэг */
    val selectTag: String? = null,

    /** Посты с тегом */
    val posts: Flow<PagingData<PostDomain>> = emptyFlow(),
)