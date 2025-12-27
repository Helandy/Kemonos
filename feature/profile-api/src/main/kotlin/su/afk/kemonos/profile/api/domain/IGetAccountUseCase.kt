package su.afk.kemonos.profile.api.domain

import su.afk.kemonos.profile.api.model.Login

interface IGetAccountUseCase {
    suspend operator fun invoke(): Result<Login>
}