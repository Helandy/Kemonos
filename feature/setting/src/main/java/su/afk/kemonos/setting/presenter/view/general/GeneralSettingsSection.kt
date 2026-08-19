package su.afk.kemonos.setting.presenter.view.general

import su.afk.kemonos.domain.displayName
import su.afk.kemonos.domain.SiteCatalog
import su.afk.kemonos.domain.SelectedSite
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.AppThemeMode
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.preferences.ui.RandomButtonPlacement
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.common.settingsSegmentedButtonColors
import su.afk.kemonos.setting.presenter.view.common.settingsSwitchColors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GeneralSettingsSection(
    suggestRandomAuthors: Boolean,
    onSuggestRandomAuthors: (Boolean) -> Unit,
    defaultSite: SelectedSite,
    onDefaultSiteChanged: (SelectedSite) -> Unit,
    appThemeMode: AppThemeMode,
    onAppThemeMode: (AppThemeMode) -> Unit,
    hapticFeedbackEnabled: Boolean,
    onHapticFeedbackEnabled: (Boolean) -> Unit,
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
                        text = stringResource(R.string.settings_haptic_feedback_title),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.settings_haptic_feedback_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Switch(
                    checked = hapticFeedbackEnabled,
                    onCheckedChange = onHapticFeedbackEnabled,
                    colors = settingsSwitchColors(),
                )
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            DefaultSiteSetting(
                defaultSite = defaultSite,
                onDefaultSiteChanged = onDefaultSiteChanged,
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

@SuppressLint("NonObservableLocale")
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
private fun DefaultSiteSetting(
    defaultSite: SelectedSite,
    onDefaultSiteChanged: (SelectedSite) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

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
                    text = defaultSite.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                SiteCatalog.availableSites.forEach { site ->
                    DropdownMenuItem(
                        text = { Text(text = site.displayName) },
                        onClick = {
                            expanded = false
                            onDefaultSiteChanged(site)
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
