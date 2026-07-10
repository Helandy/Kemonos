package su.afk.kemonos.utils.posts

import androidx.paging.PagingData
import androidx.paging.filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.domain.models.PostDomain.Companion.stableKey

fun Flow<PagingData<PostDomain>>.distinctPosts(): Flow<PagingData<PostDomain>> =
    map { pagingData ->
        val seen = HashSet<String>()
        pagingData.filter { post -> seen.add(post.stableKey()) }
    }
