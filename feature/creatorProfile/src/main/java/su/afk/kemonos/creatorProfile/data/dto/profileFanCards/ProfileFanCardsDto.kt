package su.afk.kemonos.creatorProfile.data.dto.profileFanCards


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileFanCards.ProfileFanCard

internal data class ProfileFanCardsDto(
    @SerializedName("added")
    val added: String,
    @SerializedName("ctime")
    val ctime: String,
    @SerializedName("ext")
    val ext: String,
    @SerializedName("file_id")
    val fileId: Int,
    @SerializedName("hash")
    val hash: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("last_checked_at")
    val lastCheckedAt: String,
    @SerializedName("mime")
    val mime: String,
    @SerializedName("mtime")
    val mtime: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("server")
    val server: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("user_id")
    val userId: String
) {
    companion object {
        fun ProfileFanCardsDto.toDomain(): ProfileFanCard = ProfileFanCard(
            added = added,
            ctime = ctime,
            ext = ext,
            fileId = fileId,
            hash = hash,
            id = id,
            lastCheckedAt = lastCheckedAt,
            mime = mime,
            mtime = mtime,
            path = path,
            price = price,
            server = server,
            size = size,
            userId = userId
        )

        fun List<ProfileFanCardsDto>.toDomain(): List<ProfileFanCard> = map { it.toDomain() }
    }
}