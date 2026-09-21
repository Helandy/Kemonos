package su.afk.kemonos.creatorProfile.presenter.creatorProfile.view.batchDownload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R

/**
 * Подтверждение массовой загрузки: одна папка на всю пачку и один общий ответ
 * про обложки для всех выбранных работ.
 */
@Composable
internal fun BatchDownloadDialog(
    selectedCount: Int,
    folderName: String,
    includeCovers: Boolean,
    inProgress: Boolean,
    onFolderNameChange: (String) -> Unit,
    onIncludeCoversChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!inProgress) onDismiss() },
        title = { Text(stringResource(R.string.batch_download_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.batch_download_summary, selectedCount),
                    style = MaterialTheme.typography.bodyMedium,
                )

                OutlinedTextField(
                    value = folderName,
                    onValueChange = onFolderNameChange,
                    enabled = !inProgress,
                    singleLine = true,
                    label = { Text(stringResource(R.string.batch_download_folder_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.batch_download_include_covers),
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Text(
                            text = stringResource(R.string.batch_download_include_covers_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Switch(
                        checked = includeCovers,
                        enabled = !inProgress,
                        onCheckedChange = onIncludeCoversChange,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !inProgress && selectedCount > 0,
            ) {
                Text(stringResource(R.string.batch_download_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !inProgress,
            ) {
                Text(stringResource(R.string.batch_download_cancel))
            }
        },
    )
}
