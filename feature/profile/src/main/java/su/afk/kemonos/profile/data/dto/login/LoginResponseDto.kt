package su.afk.kemonos.profile.data.dto.login

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.profile.api.model.Login

internal data class LoginResponseDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("role")
    val role: String,
) {
    companion object {
        fun LoginResponseDto.toDomain() = Login(
            id = id,
            username = username,
            createdAt = createdAt,
            role = role,
        )
    }
}