package su.afk.kemonos.profile.data.dto.login

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("username")
    val username: String = "",

    @SerializedName("password")
    val password: String = "",
)