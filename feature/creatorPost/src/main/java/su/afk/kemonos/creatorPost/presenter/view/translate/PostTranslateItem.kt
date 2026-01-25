package su.afk.kemonos.creatorPost.presenter.view.translate

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.preferences.ui.DateFormatMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PostTranslateItem(
    dateMode: DateFormatMode,
    published: String?,
    edited: String?,
    added: String?,
    showButtonTranslate: Boolean,
    body: String,

    expanded: Boolean,
    loading: Boolean,
    translated: String?,
    error: String?,

    onToggleTranslate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboard.current
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                published?.let {
                    Text(
                        text = "📅 ${it.toUiDateTime(dateMode)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                edited
                    ?.takeIf { it != published }
                    ?.let {
                        Text(
                            text = "✏️ ${it.toUiDateTime(dateMode)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
            }

            if (showButtonTranslate) {
                TextButton(
                    enabled = !loading,
                    onClick = onToggleTranslate
                ) {
                    if (loading) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.translate_in_progress))
                    } else {
                        Text(stringResource(if (expanded) R.string.translate_hide else R.string.translate_show))
                    }
                }

                Spacer(Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        scope.launch {
                            val clip = ClipData.newPlainText("post", body)
                            clipboard.setClipEntry(ClipEntry(clip))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = stringResource(R.string.copy),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AnimatedVisibility(visible = expanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.translate_title),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )

                        TooltipBox(
                            state = tooltipState,
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.translate_quality_hint))
                                }
                            }
                        ) {
                            IconButton(
                                onClick = {
                                    scope.launch { tooltipState.show() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = stringResource(R.string.translate_quality_hint),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            enabled = !translated.isNullOrBlank(),
                            onClick = {
                                scope.launch {
                                    val clip = ClipData.newPlainText("translation", translated.orEmpty())
                                    clipboard.setClipEntry(ClipEntry(clip))
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }

                    }

                    Spacer(Modifier.height(8.dp))
                    when {
                        loading -> Text(
                            stringResource(R.string.translate_model_loading),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        error != null -> Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )

                        translated != null -> Text(
                            translated,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
