package su.afk.kemonos.profile.presenter.setting.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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

        /** GitHub (под кнопками сайтов) */
        GitHubButton(
            appVersion = appVersion,
            onClick = onGitHubClick
        )
    }
}

