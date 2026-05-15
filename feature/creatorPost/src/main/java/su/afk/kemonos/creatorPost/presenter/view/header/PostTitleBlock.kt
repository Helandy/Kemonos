package su.afk.kemonos.creatorPost.presenter.view.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R

@Composable
internal fun PostTitleBlock(
    title: String?,
    showPreviewNames: Boolean,
    onTogglePreviewNames: () -> Unit,
    showAttachmentsAction: Boolean,
    onShowAttachmentsClick: () -> Unit,
    onDownloadAllClick: () -> Unit,
    onShareClick: () -> Unit,
    onCopyOriginalClick: () -> Unit,
    showCreatorBannerAction: Boolean,
    onShowCreatorBannerClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                maxLines = 3,
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
                    text = { Text(stringResource(R.string.download_all)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onDownloadAllClick()
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

                if (showCreatorBannerAction) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.show_creator_banner)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AccountBox,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onShowCreatorBannerClick()
                        }
                    )
                }

                if (showAttachmentsAction) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.show_attachments_block)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onShowAttachmentsClick()
                        }
                    )
                }

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
