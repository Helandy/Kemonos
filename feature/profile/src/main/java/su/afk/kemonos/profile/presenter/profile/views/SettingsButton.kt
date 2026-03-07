package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.profile.R

@Composable
internal fun SettingsButton(
    onClick: () -> Unit,
) {
    ProfileActionCard(
        title = stringResource(R.string.profile_settings_button),
        icon = Icons.Outlined.Settings,
        onClick = onClick,
    )
}
