package su.afk.kemonos.domain.models.dms

data class DmDomain(
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
