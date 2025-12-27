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
import androidx.compose.ui.unit.dp
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

            if (isLoggedIn && login != null) {
                /** Состояние: пользователь залогинен */
                Text(
                    text = stringResource(
                        R.string.profile_account_logged_in_as,
                        login.username
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(
                        R.string.profile_account_joined_role,
                        login.createdAt,
                    ),
                    style = MaterialTheme.typography.bodySmall
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