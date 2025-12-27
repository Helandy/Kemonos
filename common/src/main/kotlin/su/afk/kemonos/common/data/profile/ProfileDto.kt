package su.afk.kemonos.common.data.profile

import kotlinx.serialization.SerialName
import su.afk.kemonos.domain.domain.models.Profile

data class ProfileDto(
    @SerialName("has_chats")
    val has_chats: Boolean?,
    @SerialName("id")
    val id: String,
    @SerialName("indexed")
    val indexed: String?,
    @SerialName("name")
    val name: String,
    @SerialName("public_id")
    val public_id: String?,
    @SerialName("relation_id")
    val relation_id: Int?,
    @SerialName("service")
    val service: String,
    @SerialName("updated")
    val updated: String?,
    @SerialName("post_count")
    val post_count: Int?,
    @SerialName("dm_count")
    val dm_count: Int?,
    @SerialName("share_count")
    val share_count: Int?,
    @SerialName("chat_count")
    val chat_count: Int?,
) {
    companion object {
        fun ProfileDto.toDomain(): Profile = Profile(
            hasChats = this.has_chats,
            id = this.id,
            indexed = this.indexed.orEmpty(),
            name = this.name,
            publicId = this.public_id,
            relationId = this.relation_id,
            service = this.service,
            updated = this.updated.orEmpty(),
            postCount = this.post_count,
            dmCount = this.dm_count,
            shareCount = this.share_count,
            chatCount = this.chat_count,
        )
    }
}