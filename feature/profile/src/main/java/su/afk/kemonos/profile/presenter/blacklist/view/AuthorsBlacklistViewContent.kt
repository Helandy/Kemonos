package su.afk.kemonos.profile.presenter.blacklist.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.profile.R
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.utils.search.normalizeSearchQuery

@Composable
internal fun RemoveAuthorDialog(
    author: BlacklistedAuthor,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.profile_authors_blacklist_remove_title)) },
        text = {
            Text(
                stringResource(
                    R.string.profile_authors_blacklist_remove_message,
                    author.creatorName
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.profile_authors_blacklist_remove_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

internal fun List<BlacklistedAuthor>.filteredByQuery(query: String): List<BlacklistedAuthor> {
    if (query.isBlank()) return this

    val normalizedQuery = query.normalizeSearchQuery()
    return filter { item ->
        item.creatorName.contains(normalizedQuery, ignoreCase = true) ||
                item.service.contains(normalizedQuery, ignoreCase = true) ||
                item.creatorId.contains(normalizedQuery, ignoreCase = true)
    }
}