package su.afk.kemonos.setting.presenter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.uiSetting.CreatorProfileTabsOrderEditor
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatorTabsOrderScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = false,
        isLoading = false,
        topBarScroll = TopBarScroll.Pinned,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.settings_ui_creator_profile_tabs_sort_title),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_ui_creator_profile_tabs_sort_hint),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CreatorProfileTabsOrderEditor(
                value = state.uiSettingModel.creatorProfileTabsOrder,
                onChange = { onEvent(Event.ChangeViewSetting.EditCreatorProfileTabsOrder(it)) }
            )
        }
    }
}
