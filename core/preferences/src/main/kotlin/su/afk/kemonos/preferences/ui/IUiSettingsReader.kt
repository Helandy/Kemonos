package su.afk.kemonos.preferences.ui

import kotlinx.coroutines.flow.Flow

/** Только чтение UI-настроек. Потребителям, которые ничего не меняют, нужен именно он. */
interface IUiSettingsReader {
    val prefs: Flow<UiSettingModel>
}
