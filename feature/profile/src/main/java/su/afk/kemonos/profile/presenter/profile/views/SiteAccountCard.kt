package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.common.utilsUI.KemonoPreviewScreen
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.model.Login

/**
 * Одна карточка аккаунта (Kemono / Coomer).
 */
@Composable
internal fun SiteAccountCard(
    title: String,
    isLoggedIn: Boolean,
    login: Login?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            /** Залогинен */
            if (isLoggedIn && login != null) {
                Text(
                    text = stringResource(
                        R.string.profile_account_logged_in_name,
                        login.username,
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(
                        R.string.profile_account_logged_in_date,
                        login.createdAt.toUiDateTime()
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.profile_logout_button))
                }
            } else {
                /** Состояние: не залогинен */
                Text(
                    text = stringResource(R.string.profile_not_logged_in_message),
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.profile_login_button))
                }
            }
        }
    }
}

@Preview("SiteAccountCardPreview")
@Composable
private fun SiteAccountCardPreview() {
    KemonoPreviewScreen {
        SiteAccountCard(
            title = "Site Account",
            isLoggedIn = true,
            login = Login(
                id = 1,
                username = "Sandy",
                createdAt = "11.11.2020",
                role = "user"
            ),
            onLoginClick = {},
            onLogoutClick = {},
        )
    }
}