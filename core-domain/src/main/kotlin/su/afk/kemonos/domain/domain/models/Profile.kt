package su.afk.kemonos.domain.domain.models

data class Profile(
    val hasChats: Boolean?,
    val id: String,
    val service: String,
    val name: String,
    val indexed: String?,
    val publicId: String?,
    val relationId: Int?,
    val updated: String?,
    val postCount: Int?,
    val dmCount: Int?,
    val shareCount: Int?,
    val chatCount: Int?,
)