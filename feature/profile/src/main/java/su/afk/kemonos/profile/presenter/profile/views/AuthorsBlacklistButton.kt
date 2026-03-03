package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.profile.R

@Composable
internal fun AuthorsBlacklistButton(
    onClick: () -> Unit,
) {
    ProfileActionCard(
        title = stringResource(R.string.profile_authors_blacklist),
        icon = Icons.Outlined.Block,
        onClick = onClick,
    )
}
