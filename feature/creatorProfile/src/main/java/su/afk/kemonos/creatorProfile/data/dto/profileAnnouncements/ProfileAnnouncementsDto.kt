package su.afk.kemonos.creatorProfile.data.dto.profileAnnouncements


import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement

internal data class ProfileAnnouncementsDto(
    @SerializedName("added")
    val added: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("hash")
    val hash: String,
    @SerializedName("service")
    val service: String,
    @SerializedName("user_id")
    val userId: String
) {
    companion object {
        fun List<ProfileAnnouncementsDto>.toDomain(): List<ProfileAnnouncement> = map { it.toDomain() }

        fun ProfileAnnouncementsDto.toDomain(): ProfileAnnouncement = ProfileAnnouncement(
            added = added,
            content = content,
            hash = hash,
            service = service,
            userId = userId
        )
    }
}