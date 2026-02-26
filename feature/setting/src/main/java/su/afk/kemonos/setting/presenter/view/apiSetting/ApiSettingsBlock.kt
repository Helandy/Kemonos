package su.afk.kemonos.setting.presenter.view.apiSetting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.setting.presenter.SettingState
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.input.BaseUrlDomainField
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@Composable
internal fun ApiSettingsBlock(
    state: SettingState.State,
    onKemonoChanged: (String) -> Unit,
    onCoomerChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.main_api_current_urls_title),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.settings_api_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Text(
                    text = "Kemono: ${state.kemonoUrl}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Coomer: ${state.coomerUrl}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.settings_api_fields_title),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(10.dp))

        BaseUrlDomainField(
            value = state.inputKemonoDomain,
            onValueChange = onKemonoChanged,
            label = { Text(stringResource(R.string.main_api_kemono_url_label)) }
        )

        Spacer(Modifier.height(10.dp))

        BaseUrlDomainField(
            value = state.inputCoomerDomain,
            onValueChange = onCoomerChanged,
            label = { Text(stringResource(R.string.main_api_coomer_url_label)) }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSave,
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
            Text(stringResource(R.string.save))
        }

        if (state.saveSuccess) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.saved),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview("PreviewApiSettingsBlock")
@Composable
private fun PreviewApiSettingsBlock() {
    KemonosPreviewScreen {
        ApiSettingsBlock(
            state = SettingState.State(),
            onKemonoChanged = {},
            onCoomerChanged = {},
            onSave = {},
        )
    }
}