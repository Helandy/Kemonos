package su.afk.kemonos.profile.data

import su.afk.kemonos.core.api.domain.net.helpers.call
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.profile.data.api.AccountApi
import su.afk.kemonos.profile.data.dto.account.AccountDto.Companion.toDomain
import javax.inject.Inject

internal interface IAccountRepository {
    suspend fun getAccount(): Result<Login>
}

internal class AccountRepository @Inject constructor(
    private val api: AccountApi
) : IAccountRepository {

    override suspend fun getAccount(): Result<Login> = runCatching {
        api.getAccount().call {
            it.toDomain()
        }
    }
}