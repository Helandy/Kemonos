package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.profile.R

@Composable
internal fun DownloadsButton(
    onClick: () -> Unit,
) {
    ProfileActionCard(
        title = stringResource(R.string.profile_downloads),
        icon = Icons.Outlined.Download,
        onClick = onClick,
    )
}
