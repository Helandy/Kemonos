package su.afk.kemonos.profile.data.dto.account

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.profile.data.dto.login.LoginResponseDto

internal data class AccountDto(
    @SerializedName("props")
    val props: PropsDto,
) {
    companion object {
        fun AccountDto.toDomain() = Login(
            id = props.account.id,
            username = props.account.username,
            createdAt = props.account.createdAt,
            role = props.account.role,
        )
    }
}

internal data class PropsDto(
    @SerializedName("currentPage")
    val currentPage: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("account")
    val account: LoginResponseDto,

    @SerializedName("notifications_count")
    val notificationsCount: Int?,
)