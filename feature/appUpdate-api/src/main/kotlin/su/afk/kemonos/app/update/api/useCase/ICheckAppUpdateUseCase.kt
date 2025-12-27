package su.afk.kemonos.app.update.api.useCase

import su.afk.kemonos.app.update.api.model.AppUpdateInfo

interface ICheckAppUpdateUseCase {
    suspend operator fun invoke(): AppUpdateInfo?
}