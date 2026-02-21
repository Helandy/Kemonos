package su.afk.kemonos.posts.domain.model.dms

internal data class DmsPageDomain(
    val count: Int,
    val limit: Int,
    val dms: List<DmDomain>,
)
