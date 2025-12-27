package su.afk.kemonos.creatorProfile.data.dto.profileDms


import com.google.gson.annotations.SerializedName

data class PropsDto(
    @SerializedName("artist")
    val artistDto: ArtistDto,
    @SerializedName("display_data")
    val displayDataDto: DisplayDataDto,
    @SerializedName("dm_count")
    val dmCount: Int,
    @SerializedName("dms")
    val dmDtos: List<DmDto>,
    @SerializedName("has_links")
    val hasLinks: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("service")
    val service: String,
    @SerializedName("share_count")
    val shareCount: Int
)