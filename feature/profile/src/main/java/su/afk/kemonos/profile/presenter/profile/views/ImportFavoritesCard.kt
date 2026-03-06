package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R

@Composable
internal fun ImportFavoritesCard(
    enabled: Boolean,
    blocked: Boolean,
    inProgress: Boolean,
    onImportArtists: () -> Unit,
    onImportPosts: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val actionEnabled = enabled && !blocked

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ProfileActionCard(
            title = stringResource(R.string.profile_menu_import_favorites),
            icon = Icons.Filled.FileUpload,
            enabled = actionEnabled,
            onClick = { expanded = true },
        )

        if (expanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        expanded = false
                        onImportArtists()
                    },
                    enabled = actionEnabled,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.profile_export_authors))
                }

                Button(
                    onClick = {
                        expanded = false
                        onImportPosts()
                    },
                    enabled = actionEnabled,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.profile_export_posts))
                }

                OutlinedButton(
                    onClick = { expanded = false },
                    enabled = !inProgress,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.profile_export_close))
                }
            }
        }

        if (inProgress) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
