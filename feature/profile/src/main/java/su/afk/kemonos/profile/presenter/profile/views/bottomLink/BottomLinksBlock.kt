package su.afk.kemonos.profile.presenter.profile.views.bottomLink

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.deepLink.utils.openUrlInBrowser
import su.afk.kemonos.preferences.domainResolver.LocalDomainResolver
import su.afk.kemonos.profile.BuildConfig
import su.afk.kemonos.profile.R
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@Composable
internal fun BottomLinksBlock() {
    val context = LocalContext.current
    val resolver = LocalDomainResolver.current
    val kemonoUrl = resolver.baseUrlByService("patreon")
    val coomerUrl = resolver.baseUrlByService("onlyfans")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { openUrlInBrowser(context, kemonoUrl) },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Kemono",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = { openUrlInBrowser(context, coomerUrl) },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Coomer",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.settings_github_ideas),
        )

        Button(
            onClick = { openUrlInBrowser(context, "https://github.com/Helandy/Kemonos") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(
                text = stringResource(su.afk.kemonos.ui.R.string.profile_app_version, BuildConfig.VERSION_NAME),
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
    KemonosPreviewScreen {
        BottomLinksBlock()
    }
}
