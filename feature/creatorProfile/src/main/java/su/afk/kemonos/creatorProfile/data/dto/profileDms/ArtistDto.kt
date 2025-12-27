package su.afk.kemonos.creatorProfile.data.dto.profileDms


import com.google.gson.annotations.SerializedName

data class ArtistDto(
    @SerializedName("has_chats")
    val hasChats: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("indexed")
    val indexed: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("public_id")
    val publicId: String,
    @SerializedName("relation_id")
    val relationId: Int,
    @SerializedName("service")
    val service: String,
    @SerializedName("updated")
    val updated: String
)