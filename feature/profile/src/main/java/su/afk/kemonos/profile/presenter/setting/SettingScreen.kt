package su.afk.kemonos.profile.presenter.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.common.utilsUI.KemonosPreviewScreen
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.setting.SettingState.*
import su.afk.kemonos.profile.presenter.setting.view.apiSetting.ApiSettingsBlock
import su.afk.kemonos.profile.presenter.setting.view.bottomLink.BottomLinksBlock
import su.afk.kemonos.profile.presenter.setting.view.cache.CacheSettingsBlock
import su.afk.kemonos.profile.presenter.setting.view.faq.FaqBlock
import su.afk.kemonos.profile.presenter.setting.view.uiSetting.UISettingBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(state: State, onEvent: (Event) -> Unit, effect: Flow<Effect>) {
    val uriHandler = LocalUriHandler.current

    BaseScreen(
        contentModifier = Modifier.padding(horizontal = 8.dp),
        isScroll = true,
        isLoading = state.loading,
        topBarScroll = TopBarScroll.ExitUntilCollapsed,
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { onEvent(Event.Back) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.register_button_back),
                    )
                }
                Text(
                    text = stringResource(R.string.setting),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.width(48.dp))
            }
        },
    ) {
        UISettingBlock(state = state, onEvent = onEvent)

        Spacer(Modifier.height(16.dp))

        ApiSettingsBlock(
            state = state,
            onKemonoChanged = { onEvent(Event.ApiSetting.InputKemonoDomainChanged(it)) },
            onCoomerChanged = { onEvent(Event.ApiSetting.InputCoomerDomainChanged(it)) },
            onSave = { onEvent(Event.ApiSetting.SaveUrls) }
        )

        Spacer(Modifier.height(16.dp))

        CacheSettingsBlock(
            state = state,
            dateFormatMode = state.uiSettingModel.dateFormatMode,
            onEvent = onEvent
        )

        Spacer(Modifier.height(16.dp))

        FaqBlock()

        Spacer(Modifier.height(16.dp))

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

@Preview("PreviewSettingScreen")
@Composable
private fun PreviewSettingScreen() {
    KemonosPreviewScreen {
        SettingScreen(
            state = State().copy(loading = false),
            onEvent = {},
            effect = emptyFlow(),
        )
    }
}
