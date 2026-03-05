package su.afk.kemonos.posts.domain.model.dms

import su.afk.kemonos.posts.api.dms.DmDomain

internal data class DmsPageDomain(
    val count: Int,
    val limit: Int,
    val dms: List<DmDomain>,
) {
    companion object {
        const val UNKNOWN_COUNT = -1
    }
}
