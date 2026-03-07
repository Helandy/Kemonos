package su.afk.kemonos.profile.data.repository

import su.afk.kemonos.network.util.call
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.profile.data.api.AccountApi
import su.afk.kemonos.profile.data.dto.account.AccountDto.Companion.toDomain
import su.afk.kemonos.profile.domain.repository.IAccountRepository
import javax.inject.Inject

internal class AccountRepository @Inject constructor(
    private val api: AccountApi
) : IAccountRepository {

    override suspend fun getAccount(): Result<Login> = runCatching {
        api.getAccount().call {
            it.toDomain()
        }
    }
}
