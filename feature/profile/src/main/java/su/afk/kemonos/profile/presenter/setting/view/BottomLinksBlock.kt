package su.afk.kemonos.profile.presenter.setting.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import su.afk.kemonos.profile.R

@Composable
internal fun BottomLinksBlock(
    kemonoUrl: String,
    coomerUrl: String,
    appVersion: String,
    onGitHubClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /** кнопки сайтов (сверху блока) */
        LinksButtonsRow(
            kemonoUrl = kemonoUrl,
            coomerUrl = coomerUrl
        )

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.settings_github_ideas),
        )

        /** GitHub (под кнопками сайтов) */
        GitHubButton(
            appVersion = appVersion,
            onClick = onGitHubClick
        )
    }
}

