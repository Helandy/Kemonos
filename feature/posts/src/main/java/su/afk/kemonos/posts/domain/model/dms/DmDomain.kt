package su.afk.kemonos.posts.domain.model.dms

internal data class DmDomain(
    val added: String,
    val content: String,
    val hash: String,
    val published: String,
    val service: String,
    val user: String,
    val artistId: String,
    val artistName: String,
    val artistUpdated: String?,
)
