package su.afk.kemonos.profile.presenter.setting.view.bottomLink

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.utilsUI.KemonoPreviewScreen
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.profile.R

@Composable
internal fun BottomLinksBlock(
    kemonoUrl: String,
    coomerUrl: String,
    appVersion: String,
    onGitHubClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /** кнопки сайтов (сверху блока) */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { if (kemonoUrl.isNotBlank()) uriHandler.openUri(kemonoUrl) },
                modifier = Modifier.weight(1f),
                enabled = kemonoUrl.isNotBlank()
            ) { Text("Kemono", maxLines = 1, overflow = TextOverflow.Ellipsis) }

            OutlinedButton(
                onClick = { if (coomerUrl.isNotBlank()) uriHandler.openUri(coomerUrl) },
                modifier = Modifier.weight(1f),
                enabled = coomerUrl.isNotBlank()
            ) { Text("Coomer", maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.settings_github_ideas),
        )

        /** GitHub (под кнопками сайтов) */
        Button(
            onClick = onGitHubClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(
                text = stringResource(su.afk.kemonos.common.R.string.profile_app_version, appVersion),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null
            )
        }
    }
}

@Preview("PreviewBottomLinksBlock")
@Composable
private fun PreviewBottomLinksBlock() {
    KemonoPreviewScreen {
        BottomLinksBlock(
            kemonoUrl = "",
            coomerUrl = "",
            appVersion = BuildConfig.VERSION_NAME,
            onGitHubClick = {},
        )
    }
}