package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.apiSetting.ApiSettingsBlock
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingNetworkScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = true,
        isLoading = state.loading,
        topBarScroll = TopBarScroll.Pinned,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.settings_hub_network_title),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        ApiSettingsBlock(
            state = state,
            onKemonoChanged = { onEvent(Event.ApiSetting.InputKemonoDomainChanged(it)) },
            onCoomerChanged = { onEvent(Event.ApiSetting.InputCoomerDomainChanged(it)) },
            onSave = { onEvent(Event.ApiSetting.SaveUrls) },
        )
    }
}
