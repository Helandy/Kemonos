package su.afk.kemonos.profile.presenter.faq

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun FaqScreen(
    onBack: () -> Unit,
) {
    BaseScreen(
        isScroll = true,
        contentPadding = PaddingValues(horizontal = 12.dp),
        topBarScroll = TopBarScroll.EnterAlways,
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.profile_faq_screen_title),
                onBack = onBack,
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FaqItemCard(
                icon = Icons.Outlined.Download,
                title = stringResource(R.string.profile_faq_q1_title),
                body = stringResource(R.string.profile_faq_q1_body),
            )
            FaqItemCard(
                icon = Icons.Outlined.PlayCircleOutline,
                title = stringResource(R.string.profile_faq_q2_title),
                body = stringResource(R.string.profile_faq_q2_body),
            )
            FaqItemCard(
                icon = Icons.Outlined.Headset,
                title = stringResource(R.string.profile_faq_q3_title),
                body = stringResource(R.string.profile_faq_q3_body),
            )
            FaqItemCard(
                icon = Icons.Outlined.BugReport,
                title = stringResource(R.string.profile_faq_q4_title),
                body = stringResource(R.string.profile_faq_q4_body),
            )
            FaqItemCard(
                icon = Icons.Outlined.ErrorOutline,
                title = stringResource(R.string.profile_faq_q5_title),
                body = stringResource(R.string.profile_faq_q5_body),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FaqItemCard(
    icon: ImageVector,
    title: String,
    body: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(8.dp),
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "FaqScreen")
@Composable
private fun PreviewFaqScreen() {
    KemonosPreviewScreen {
        FaqScreen(onBack = {})
    }
}
