package su.afk.kemonos.profile.presenter.profile.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DateFormatMode
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

/**
 * Одна карточка аккаунта (Kemono / Coomer).
 */
@Composable
internal fun SiteAccountCard(
    dateMode: DateFormatMode,
    title: String,
    isLoggedIn: Boolean,
    login: Login?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(38.dp)
                            .padding(7.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (isLoggedIn && login != null) {
                Text(
                    text = stringResource(R.string.profile_account_logged_in_name, login.username),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(
                        R.string.profile_account_logged_in_date,
                        login.createdAt.toUiDateTime(dateMode),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.profile_logout_button))
                }
            } else {
                Text(
                    text = stringResource(R.string.profile_not_logged_in_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.login_button_sign_in))
                }
            }
        }
    }
}

@Preview("SiteAccountCardPreview")
@Composable
private fun SiteAccountCardPreview() {
    KemonosPreviewScreen {
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
            dateMode = DateFormatMode.DD_MM_YYYY
        )
    }
}
