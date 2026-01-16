package su.afk.kemonos.preferences.ui

import kotlinx.coroutines.flow.Flow

interface IUiSettingUseCase {
    val prefs: Flow<UiSettingModel>

    /** Вид отображения авторов  */
    suspend fun setCreatorsViewMode(value: CreatorViewMode)

    /** Debug: пропустить проверку API при входе */
    suspend fun setSkipApiCheckOnLogin(value: Boolean)
}