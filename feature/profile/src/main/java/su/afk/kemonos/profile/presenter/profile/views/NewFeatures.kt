package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R

@Composable
fun NewFeatures() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
            ,
            headlineContent = {
                Text(text = stringResource(R.string.profile_menu_keys))
            }
        )

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
            ,
            headlineContent = {
                Text(text = stringResource(R.string.profile_menu_review_dms))
            }
        )

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
            ,
            headlineContent = {
                Text(text = stringResource(R.string.profile_menu_export_favorites))
            }
        )

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
            ,
            headlineContent = {
                Text(text = stringResource(R.string.profile_menu_change_username))
            }
        )

        ListItem(
            modifier = Modifier
                .fillMaxWidth()
            ,
            headlineContent = {
                Text(text = stringResource(R.string.profile_menu_change_password))
            }
        )
    }
}