package su.afk.kemonos.creatorProfile.data.dto.profileDms


import com.google.gson.annotations.SerializedName

data class DisplayDataDto(
    @SerializedName("href")
    val href: String,
    @SerializedName("service")
    val service: String
)