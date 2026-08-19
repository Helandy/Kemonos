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
import su.afk.kemonos.domain.SiteCatalog
import su.afk.kemonos.domain.displayName
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
                    state.uiSettingModel.enabledSiteList.forEach { site ->
                        CurrentUrlText("${site.displayName}: ${state.siteUrl(site)}")
                        CurrentUrlText(
                            stringResource(
                                R.string.settings_media_image_current,
                                state.imageHostUrls[site].orEmpty(),
                            )
                        )
                        CurrentUrlText(
                            stringResource(
                                R.string.settings_media_file_current,
                                state.fileHostUrls[site].orEmpty(),
                            )
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

            state.uiSettingModel.enabledSiteList.forEachIndexed { index, site ->
                if (index > 0) Spacer(Modifier.height(16.dp))

                BaseUrlDomainField(
                    value = state.inputDomain(site),
                    onValueChange = { onEvent(Event.ApiSetting.InputDomainChanged(site, it)) },
                    label = {
                        Text(
                            stringResource(
                                su.afk.kemonos.ui.R.string.main_api_site_url_label,
                                site.displayName,
                            )
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.settings_media_hosts_title, site.displayName),
                    style = MaterialTheme.typography.titleSmall,
                )

                Text(
                    text = stringResource(R.string.settings_media_hosts_description, site.displayName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(8.dp))

                BaseUrlDomainField(
                    value = state.inputImageHostOverrides[site].orEmpty(),
                    onValueChange = { onEvent(Event.ApiSetting.InputImageHostChanged(site, it)) },
                    label = { Text(stringResource(R.string.settings_media_image_host_label)) },
                    suffixText = null,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.settings_media_host_automatic,
                                state.imageHostUrls[site].orEmpty(),
                            )
                        )
                    },
                )

                Spacer(Modifier.height(8.dp))

                BaseUrlDomainField(
                    value = state.inputFileHostOverrides[site].orEmpty(),
                    onValueChange = { onEvent(Event.ApiSetting.InputFileHostChanged(site, it)) },
                    label = { Text(stringResource(R.string.settings_media_file_host_label)) },
                    suffixText = null,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.settings_media_host_automatic,
                                state.fileHostUrls[site].orEmpty(),
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
    SiteCatalog.availableSites.forEach { site ->
        val checked = site in enabledSites
        SwitchRow(
            title = site.displayName,
            checked = checked,
            enabled = enabledSites.size > 1 || !checked,
            onCheckedChange = { onToggle(site, it) },
        )
    }
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
