package su.afk.kemonos.main.presenter.delegates

import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.app.update.api.useCase.ICheckAppUpdateUseCase
import javax.inject.Inject

internal class AppUpdateGateDelegate @Inject constructor(
    private val checkAppUpdateUseCase: ICheckAppUpdateUseCase,
) {
    suspend fun check(): AppUpdateInfo? = checkAppUpdateUseCase()
}