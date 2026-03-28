package su.afk.kemonos.setting.presenter.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.domain.model.TranslateModelInfo
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.setting.presenter.view.common.SectionSpacer
import su.afk.kemonos.setting.presenter.view.common.SettingsSectionTitle
import su.afk.kemonos.setting.presenter.view.translate.displayTranslateLanguage
import su.afk.kemonos.ui.uiUtils.size.formatBytes

@Composable
internal fun SettingTranslateModelsScreen(
    state: SettingState.State,
    onEvent: (SettingState.Event) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(SettingState.Event.RefreshTranslateModels)
    }

    SettingsScreenScaffold(
        title = stringResource(R.string.settings_translate_models_title),
        onBack = { onEvent(SettingState.Event.Back) },
        isLoading = state.loading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
    ) {
        SectionSpacer()
        SettingsSectionTitle(text = stringResource(R.string.settings_translate_models_title))
        Spacer(Modifier.height(6.dp))

        if (state.translateModelsLoading) {
            Text(
                text = stringResource(R.string.settings_translate_models_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            return@SettingsScreenScaffold
        }

        if (state.translateModels.isEmpty()) {
            Text(
                text = stringResource(R.string.settings_translate_models_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            return@SettingsScreenScaffold
        }

        state.translateModels.forEachIndexed { index, model ->
            TranslateModelRow(
                model = model,
                deleting = state.deletingTranslateModelId == model.id,
                onDelete = { onEvent(SettingState.Event.DeleteTranslateModel(model.id)) },
            )
            if (index != state.translateModels.lastIndex) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TranslateModelRow(
    model: TranslateModelInfo,
    deleting: Boolean,
    onDelete: () -> Unit,
) {
    val source = displayTranslateLanguage(model.sourceLanguageTag)
    val target = displayTranslateLanguage(model.targetLanguageTag)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.settings_translate_models_pair, source, target),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = formatBytes(model.sizeBytes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        FilledTonalButton(
            onClick = onDelete,
            enabled = !deleting,
        ) {
            if (deleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(text = stringResource(R.string.settings_translate_models_delete))
        }
    }
}

@Preview(name = "Setting Translate Models", showBackground = true)
@Composable
private fun PreviewSettingTranslateModelsScreen() {
    SettingsPreview {
        SettingTranslateModelsScreen(
            state = previewSettingState().copy(
                translateModels = listOf(
                    TranslateModelInfo(
                        id = "en_ru",
                        sourceLanguageTag = "en",
                        targetLanguageTag = "ru",
                        sizeBytes = 40L * 1024L * 1024L,
                    ),
                    TranslateModelInfo(
                        id = "en_ja",
                        sourceLanguageTag = "en",
                        targetLanguageTag = "ja",
                        sizeBytes = 61L * 1024L * 1024L,
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}
