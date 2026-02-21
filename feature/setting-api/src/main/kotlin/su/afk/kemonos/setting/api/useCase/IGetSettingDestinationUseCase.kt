package su.afk.kemonos.setting.api.useCase

import androidx.navigation3.runtime.NavKey

interface IGetSettingDestinationUseCase {
    operator fun invoke(): NavKey
}
