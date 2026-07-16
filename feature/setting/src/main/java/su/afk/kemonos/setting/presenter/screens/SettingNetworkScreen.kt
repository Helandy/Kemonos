package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.SettingState.Event
import su.afk.kemonos.setting.presenter.SettingState.State
import su.afk.kemonos.setting.presenter.view.SwitchRow
import su.afk.kemonos.ui.components.input.BaseUrlDomainField
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingNetworkScreen(
    state: State,
    onEvent: (Event) -> Unit,
) {
    SettingsScreenScaffold(
        title = stringResource(R.string.settings_hub_network_title),
        onBack = { onEvent(Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
        topBarScroll = TopBarScroll.Pinned,
    ) {
        Column {
            Text(
                text = stringResource(su.afk.kemonos.ui.R.string.main_api_current_urls_title),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(su.afk.kemonos.ui.R.string.settings_api_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.settings_api_enabled_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            EnabledApiSwitches(
                enabledSites = state.uiSettingModel.enabledSites,
                onToggle = { site, enabled -> onEvent(Event.ApiSetting.ToggleApiSite(site, enabled)) },
            )

            Spacer(Modifier.height(14.dp))

            /** Текущие URL — отдельной плашкой */
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (SelectedSite.K in state.uiSettingModel.enabledSites) {
                        CurrentUrlText("Kemono: ${state.kemonoUrl}")
                    }
                    if (SelectedSite.C in state.uiSettingModel.enabledSites) {
                        CurrentUrlText("Coomer: ${state.coomerUrl}")
                    }
                    if (SelectedSite.P in state.uiSettingModel.enabledSites) {
                        CurrentUrlText("Pawchive: ${state.pawchiveUrl}")
                        CurrentUrlText(
                            stringResource(R.string.settings_pawchive_image_current, state.pawchiveImageUrl)
                        )
                        CurrentUrlText(
                            stringResource(R.string.settings_pawchive_file_current, state.pawchiveFileUrl)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(su.afk.kemonos.ui.R.string.settings_api_fields_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(10.dp))

            if (SelectedSite.K in state.uiSettingModel.enabledSites) {
                BaseUrlDomainField(
                    value = state.inputKemonoDomain,
                    onValueChange = { onEvent(Event.ApiSetting.InputKemonoDomainChanged(it)) },
                    label = { Text(stringResource(su.afk.kemonos.ui.R.string.main_api_kemono_url_label)) }
                )
            }

            if (SelectedSite.K in state.uiSettingModel.enabledSites && SelectedSite.C in state.uiSettingModel.enabledSites) {
                Spacer(Modifier.height(10.dp))
            }

            if (SelectedSite.C in state.uiSettingModel.enabledSites) {
                BaseUrlDomainField(
                    value = state.inputCoomerDomain,
                    onValueChange = { onEvent(Event.ApiSetting.InputCoomerDomainChanged(it)) },
                    label = { Text(stringResource(su.afk.kemonos.ui.R.string.main_api_coomer_url_label)) }
                )
            }

            if ((SelectedSite.K in state.uiSettingModel.enabledSites || SelectedSite.C in state.uiSettingModel.enabledSites) &&
                SelectedSite.P in state.uiSettingModel.enabledSites
            ) {
                Spacer(Modifier.height(16.dp))
            }

            if (SelectedSite.P in state.uiSettingModel.enabledSites) {
                BaseUrlDomainField(
                    value = state.inputPawchiveDomain,
                    onValueChange = { onEvent(Event.ApiSetting.InputPawchiveDomainChanged(it)) },
                    label = { Text(stringResource(su.afk.kemonos.ui.R.string.main_api_pawchive_url_label)) }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.settings_pawchive_media_hosts_title),
                    style = MaterialTheme.typography.titleSmall,
                )

                Text(
                    text = stringResource(R.string.settings_pawchive_media_hosts_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(8.dp))

                BaseUrlDomainField(
                    value = state.inputPawchiveImageHostOverride,
                    onValueChange = {
                        onEvent(Event.ApiSetting.InputPawchiveImageHostChanged(it))
                    },
                    label = { Text(stringResource(R.string.settings_pawchive_image_host_label)) },
                    suffixText = null,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.settings_pawchive_media_host_automatic,
                                state.pawchiveImageUrl,
                            )
                        )
                    },
                )

                Spacer(Modifier.height(8.dp))

                BaseUrlDomainField(
                    value = state.inputPawchiveFileHostOverride,
                    onValueChange = {
                        onEvent(Event.ApiSetting.InputPawchiveFileHostChanged(it))
                    },
                    label = { Text(stringResource(R.string.settings_pawchive_file_host_label)) },
                    suffixText = null,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.settings_pawchive_media_host_automatic,
                                state.pawchiveFileUrl,
                            )
                        )
                    },
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { onEvent(Event.ApiSetting.SaveUrls) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Text(stringResource(su.afk.kemonos.ui.R.string.save))
            }

            if (state.saveSuccess) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(su.afk.kemonos.ui.R.string.saved),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CurrentUrlText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun EnabledApiSwitches(
    enabledSites: Set<SelectedSite>,
    onToggle: (SelectedSite, Boolean) -> Unit,
) {
    SelectedSite.entries.forEach { site ->
        val checked = site in enabledSites
        SwitchRow(
            title = site.settingsLabel(),
            checked = checked,
            enabled = enabledSites.size > 1 || !checked,
            onCheckedChange = { onToggle(site, it) },
        )
    }
}

@Composable
private fun SelectedSite.settingsLabel(): String =
    when (this) {
        SelectedSite.K -> stringResource(su.afk.kemonos.ui.R.string.main_api_kemono_label)
        SelectedSite.C -> stringResource(su.afk.kemonos.ui.R.string.main_api_coomer_label)
        SelectedSite.P -> stringResource(su.afk.kemonos.ui.R.string.main_api_pawchive_label)
    }

@Preview(name = "Setting Network", showBackground = true)
@Composable
private fun PreviewSettingNetworkScreen() {
    SettingsPreview {
        SettingNetworkScreen(
            state = previewSettingState(),
            onEvent = {},
        )
    }
}
