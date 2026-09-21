package su.afk.kemonos.setting.presenter.view.download

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DownloadFileNameMode
import su.afk.kemonos.setting.R
import su.afk.kemonos.setting.presenter.view.common.settingsRadioButtonColors

@Composable
internal fun DownloadFileNameModeRow(
    title: String,
    value: DownloadFileNameMode,
    onChange: (DownloadFileNameMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(8.dp))

        DownloadFileNameMode.entries.forEachIndexed { index, mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChange(mode) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = value == mode,
                    onClick = { onChange(mode) },
                    colors = settingsRadioButtonColors(),
                )

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (mode) {
                            DownloadFileNameMode.ORIGINAL ->
                                stringResource(R.string.settings_download_file_name_original)

                            DownloadFileNameMode.NUMBERED ->
                                stringResource(R.string.settings_download_file_name_numbered)

                            DownloadFileNameMode.NUMBERED_FOR_HASH_NAMES ->
                                stringResource(R.string.settings_download_file_name_numbered_hash)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = when (mode) {
                            DownloadFileNameMode.ORIGINAL ->
                                stringResource(R.string.settings_download_file_name_original_example)

                            DownloadFileNameMode.NUMBERED ->
                                stringResource(R.string.settings_download_file_name_numbered_example)

                            DownloadFileNameMode.NUMBERED_FOR_HASH_NAMES ->
                                stringResource(R.string.settings_download_file_name_numbered_hash_example)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }

            if (index != DownloadFileNameMode.entries.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}
