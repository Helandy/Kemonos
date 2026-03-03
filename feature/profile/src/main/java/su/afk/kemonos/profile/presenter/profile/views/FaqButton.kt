package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.profile.R

@Composable
internal fun FaqButton(
    onClick: () -> Unit,
) {
    ProfileActionCard(
        title = stringResource(R.string.profile_faq_screen_title),
        icon = Icons.AutoMirrored.Outlined.HelpOutline,
        onClick = onClick,
    )
}
