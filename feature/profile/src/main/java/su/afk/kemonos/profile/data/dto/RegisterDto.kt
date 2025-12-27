package su.afk.kemonos.profile.data.dto

import com.google.gson.annotations.SerializedName

data class RegisterDto(
    @SerializedName("username")
    val username: String = "",
    @SerializedName("password")
    val password: String = "",
    @SerializedName("confirm_password")
    val confirmPassword: String = "",
    @SerializedName("favorites_json")
    val favoritesJson: String = ""
)