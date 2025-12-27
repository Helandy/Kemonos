package su.afk.kemonos.creatorProfile.presenter.view

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import java.time.LocalDateTime

@Composable
fun AnnouncementsScreen(
    announcements: List<ProfileAnnouncement>,
    modifier: Modifier = Modifier
) {
    val sortedAnnouncements = announcements.sortedByDescending { ann ->
        runCatching { LocalDateTime.parse(ann.added) }.getOrNull() ?: LocalDateTime.MIN
    }

    if (sortedAnnouncements.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.announcements_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sortedAnnouncements) { announcement ->
                AnnouncementCard(announcement)
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: ProfileAnnouncement) {
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            LocalContext.current
            LocalDensity.current

            AndroidView(
                factory = { ctx ->
                    TextView(ctx).apply {
                        text = HtmlCompat.fromHtml(
                            announcement.content,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                        /** Поддержка кликабельных ссылок */
                        movementMethod = LinkMovementMethod.getInstance()
                        setTextColor(textColor)
                        /** Размер шрифта и стили */
                        textSize = 16f
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            /** Остальной контент */
            Text(
                text = stringResource(R.string.dm_added, announcement.added.toUiDateTime()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}