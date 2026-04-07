package su.afk.kemonos.main.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import su.afk.kemonos.main.api.IMainSettingsSync
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MainSettingsSync @Inject constructor(
    private val uiSettingUseCase: IUiSettingUseCase,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    @param:Named("AppScope") private val appScope: CoroutineScope,
) : IMainSettingsSync {

    private var syncJob: Job? = null

    override fun start() {
        if (syncJob != null) return

        syncJob = uiSettingUseCase.prefs
            .map { it.defaultSite }
            .distinctUntilChanged()
            .onEach { site ->
                selectedSiteUseCase.setSite(site)
            }
            .launchIn(appScope)
    }
}
