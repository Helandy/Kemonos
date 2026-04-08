package su.afk.kemonos.setting.presenter.view.general

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.AppThemeMode
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.preferences.ui.RandomButtonPlacement
import su.afk.kemonos.preferences.ui.SiteDisplayMode
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.common.settingsSegmentedButtonColors
import su.afk.kemonos.setting.presenter.view.common.settingsSwitchColors
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GeneralSettingsSection(
    suggestRandomAuthors: Boolean,
    onSuggestRandomAuthors: (Boolean) -> Unit,
    showKemono: Boolean,
    showCoomer: Boolean,
    defaultSite: SelectedSite,
    onSiteDisplayModeChanged: (SiteDisplayMode) -> Unit,
    appThemeMode: AppThemeMode,
    onAppThemeMode: (AppThemeMode) -> Unit,
    dateFormatMode: DateFormatMode,
    onDateFormatMode: (DateFormatMode) -> Unit,
    randomButtonPlace: RandomButtonPlacement,
    onRandomButtonPlace: (RandomButtonPlacement) -> Unit,
) {
    SectionSpacer()
    SettingsSectionTitle(text = stringResource(R.string.settings_ui_general_title))
    Spacer(Modifier.height(8.dp))

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ThemeModeSetting(
                value = appThemeMode,
                onChange = onAppThemeMode,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            DateFormatSetting(
                value = dateFormatMode,
                onChange = onDateFormatMode,
            )
        }
    }

    Spacer(Modifier.height(12.dp))

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            SiteDisplayModeSetting(
                showKemono = showKemono,
                showCoomer = showCoomer,
                defaultSite = defaultSite,
                onSiteDisplayModeChanged = onSiteDisplayModeChanged,
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_ui_suggest_random_authors_title),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Switch(
                    checked = suggestRandomAuthors,
                    onCheckedChange = onSuggestRandomAuthors,
                    colors = settingsSwitchColors(),
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            RandomPlacementSetting(
                value = randomButtonPlace,
                onChange = onRandomButtonPlace,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeSetting(
    value: AppThemeMode,
    onChange: (AppThemeMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_theme_title),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = value == AppThemeMode.SYSTEM,
                onClick = { onChange(AppThemeMode.SYSTEM) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                colors = settingsSegmentedButtonColors(),
                label = { Text(stringResource(R.string.settings_theme_system)) },
            )
            SegmentedButton(
                selected = value == AppThemeMode.LIGHT,
                onClick = { onChange(AppThemeMode.LIGHT) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                colors = settingsSegmentedButtonColors(),
                label = { Text(stringResource(R.string.settings_theme_light)) },
            )
            SegmentedButton(
                selected = value == AppThemeMode.DARK,
                onClick = { onChange(AppThemeMode.DARK) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                colors = settingsSegmentedButtonColors(),
                label = { Text(stringResource(R.string.settings_theme_dark)) },
            )
        }
    }
}

@Composable
private fun DateFormatSetting(
    value: DateFormatMode,
    onChange: (DateFormatMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_ui_date_format_title),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(
                    text = value.example(Locale.getDefault()),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DateFormatMode.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(mode.example(Locale.getDefault()))
                                Text(
                                    text = mode.pattern,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        onClick = {
                            expanded = false
                            onChange(mode)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SiteDisplayModeSetting(
    showKemono: Boolean,
    showCoomer: Boolean,
    defaultSite: SelectedSite,
    onSiteDisplayModeChanged: (SiteDisplayMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val currentMode = SiteDisplayMode.from(showKemono, showCoomer, defaultSite)
    val displayModes = listOf(
        SiteDisplayMode.BOTH_DEFAULT_COOMER,
        SiteDisplayMode.BOTH_DEFAULT_KEMONO,
        SiteDisplayMode.ONLY_COOMER,
        SiteDisplayMode.ONLY_KEMONO,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_default_site_title),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(8.dp))

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (currentMode) {
                        SiteDisplayMode.BOTH_DEFAULT_KEMONO -> stringResource(R.string.settings_site_display_both_default_kemono)
                        SiteDisplayMode.BOTH_DEFAULT_COOMER -> stringResource(R.string.settings_site_display_both_default_coomer)
                        SiteDisplayMode.ONLY_KEMONO -> stringResource(R.string.settings_site_display_only_kemono)
                        SiteDisplayMode.ONLY_COOMER -> stringResource(R.string.settings_site_display_only_coomer)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                displayModes.forEach { mode ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (mode) {
                                    SiteDisplayMode.BOTH_DEFAULT_KEMONO -> stringResource(R.string.settings_site_display_both_default_kemono)
                                    SiteDisplayMode.BOTH_DEFAULT_COOMER -> stringResource(R.string.settings_site_display_both_default_coomer)
                                    SiteDisplayMode.ONLY_KEMONO -> stringResource(R.string.settings_site_display_only_kemono)
                                    SiteDisplayMode.ONLY_COOMER -> stringResource(R.string.settings_site_display_only_coomer)
                                }
                            )
                        },
                        onClick = {
                            expanded = false
                            onSiteDisplayModeChanged(mode)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RandomPlacementSetting(
    value: RandomButtonPlacement,
    onChange: (RandomButtonPlacement) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_random_button_title),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = value == RandomButtonPlacement.SCREEN,
                onClick = { onChange(RandomButtonPlacement.SCREEN) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                colors = settingsSegmentedButtonColors(),
                label = { Text(stringResource(R.string.settings_random_button_screen)) },
            )
            SegmentedButton(
                selected = value == RandomButtonPlacement.SEARCH_BAR,
                onClick = { onChange(RandomButtonPlacement.SEARCH_BAR) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                colors = settingsSegmentedButtonColors(),
                label = { Text(stringResource(R.string.settings_random_button_search)) },
            )
        }
    }
}
