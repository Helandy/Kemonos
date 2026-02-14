package su.afk.kemonos.creatorProfile.presenter.view

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import su.afk.kemonos.common.R
import su.afk.kemonos.common.components.announcemnt.CoilImageGetter
import su.afk.kemonos.common.imageLoader.LocalAppImageLoader
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.preferences.ui.DateFormatMode
import java.time.LocalDateTime

@Composable
fun AnnouncementsScreen(
    dateMode: DateFormatMode,
    announcements: List<ProfileAnnouncement>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val isScrolling = remember { derivedStateOf { listState.isScrollInProgress } }

    val sortedAnnouncements = announcements.sortedByDescending { ann ->
        runCatching { LocalDateTime.parse(ann.added) }.getOrNull() ?: LocalDateTime.MIN
    }

    if (sortedAnnouncements.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.announcements_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = sortedAnnouncements,
                key = { it.hash }
            ) { announcement ->
                AnnouncementCard(
                    dateMode = dateMode,
                    announcement = announcement,
                    isScrolling = { isScrolling.value }
                )
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    dateMode: DateFormatMode,
    announcement: ProfileAnnouncement,
    isScrolling: () -> Boolean,
) {
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val scope = rememberCoroutineScope()
    val imageLoader = LocalAppImageLoader.current

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            AndroidView(
                factory = { ctx ->
                    TextView(ctx).apply {
                        setTextColor(textColor)
                        textSize = 16f
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                },
                update = { tv ->
                    tv.setTextColor(textColor)

                    val last = tv.getTag(R.id.tag_html) as? String
                    if (last != announcement.content) {
                        tv.setTag(R.id.tag_html, announcement.content)

                        val getter = CoilImageGetter(
                            textView = tv,
                            imageLoader = imageLoader,
                            scope = scope,
                            isScrolling = isScrolling,
                        )

                        tv.text = HtmlCompat.fromHtml(
                            announcement.content,
                            HtmlCompat.FROM_HTML_MODE_COMPACT,
                            getter,
                            null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 1.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.fancard_added, announcement.added.toUiDateTime(dateMode)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
