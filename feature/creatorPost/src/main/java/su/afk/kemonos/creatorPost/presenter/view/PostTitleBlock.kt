package su.afk.kemonos.creatorPost.presenter.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R

@Composable
internal fun PostTitleBlock(
    title: String?,
    showPreviewNames: Boolean,
    onTogglePreviewNames: () -> Unit,
    onShareClick: () -> Unit,
    onCopyOriginalClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
            )
        }

        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.share)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onShareClick()
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.copy_original_text)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onCopyOriginalClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(
                                if (showPreviewNames) R.string.hide_preview_file_names
                                else R.string.show_preview_file_names
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showPreviewNames) {
                                Icons.AutoMirrored.Outlined.TextSnippet
                            } else {
                                Icons.AutoMirrored.Outlined.Label
                            },
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onTogglePreviewNames()
                    }
                )
            }
        }
    }
}
