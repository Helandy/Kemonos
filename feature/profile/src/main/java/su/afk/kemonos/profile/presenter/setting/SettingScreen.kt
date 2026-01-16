package su.afk.kemonos.profile.presenter.setting

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.profile.presenter.setting.view.ApiSettingsBlock
import su.afk.kemonos.profile.presenter.setting.view.BottomLinksBlock
import su.afk.kemonos.profile.presenter.setting.view.CacheSettingsBlock
import su.afk.kemonos.profile.presenter.setting.view.FaqBlock
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.UISettingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(viewModel: SettingViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = true,
        isLoading = state.loading,
    ) {
        ApiSettingsBlock(
            state = state,
            onKemonoChanged = viewModel::onInputKemonoDomainChanged,
            onCoomerChanged = viewModel::onInputCoomerDomainChanged,
            onSave = viewModel::onSaveUrls
        )

        Spacer(Modifier.height(32.dp))

        UISettingBlock(
            state = state,
            onCreatorsViewMode = viewModel::setCreatorsViewMode,
            onSkipApiCheckOnLogin = viewModel::setSkipApiCheckOnLogin
        )

        Spacer(Modifier.height(32.dp))

        CacheSettingsBlock(
            state = state,
            formatDateTime = { ms -> ms.toUiDateTime() },
            onClear = viewModel::onClear
        )

        Spacer(Modifier.height(32.dp))

        FaqBlock()

        Spacer(Modifier.height(32.dp))

        BottomLinksBlock(
            kemonoUrl = state.kemonoUrl,
            coomerUrl = state.coomerUrl,
            appVersion = state.appVersion,
            onGitHubClick = {
                uriHandler.openUri("https://github.com/Helandy/Kemonos")
            }
        )
    }
}