package su.afk.kemonos.setting.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.setting.api.useCase.IGetSettingDestinationUseCase
import javax.inject.Inject

class GetSettingDestinationUseCase @Inject constructor() : IGetSettingDestinationUseCase {
    override fun invoke(): NavKey = SettingIntent.Open
}
