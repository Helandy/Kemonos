package su.afk.kemonos.profile.domain.repository

import su.afk.kemonos.profile.api.model.Login

internal interface IAccountRepository {
    suspend fun getAccount(): Result<Login>
}