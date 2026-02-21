package su.afk.kemonos.storage.api.repository.blacklist

data class BlacklistedAuthor(
    val service: String,
    val creatorId: String,
    val creatorName: String,
    val createdAt: Long,
)
