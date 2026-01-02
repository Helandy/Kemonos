package su.afk.kemonos.common.data.profile

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.domain.models.Profile

data class ProfileDto(
    @SerializedName("has_chats")
    val has_chats: Boolean?,
    @SerializedName("id")
    val id: String,
    @SerializedName("indexed")
    val indexed: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("public_id")
    val public_id: String?,
    @SerializedName("relation_id")
    val relation_id: Int?,
    @SerializedName("service")
    val service: String,
    @SerializedName("updated")
    val updated: String?,
    @SerializedName("post_count")
    val post_count: Int?,
    @SerializedName("dm_count")
    val dm_count: Int?,
    @SerializedName("share_count")
    val share_count: Int?,
    @SerializedName("chat_count")
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