package su.afk.kemonos.posts.domain.model.hashLookup

import su.afk.kemonos.domain.models.PostDomain

internal data class HashLookupDomain(
    val id: Long,
    val hash: String,
    val mime: String?,
    val ext: String?,
    val size: Long?,
    val posts: List<PostDomain>,
)
