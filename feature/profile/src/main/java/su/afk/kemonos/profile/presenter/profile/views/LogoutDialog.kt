package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R

@Composable
internal fun LogoutDialog(
    site: SelectedSite?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val siteName = when (site) {
        SelectedSite.K -> stringResource(R.string.kemono)
        SelectedSite.C -> stringResource(R.string.coomer)
        null -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.logout_title, siteName))
        },
        text = {
            Text(stringResource(R.string.logout_message, siteName))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.logout_confirm),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
