package su.afk.kemonos.profile.domain.account

import su.afk.kemonos.profile.api.domain.IGetAccountUseCase
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.profile.data.IAccountRepository
import javax.inject.Inject

internal class GetAccountUseCase @Inject constructor(
    private val repository: IAccountRepository
) : IGetAccountUseCase {

    override suspend operator fun invoke(): Result<Login> = repository.getAccount()
}