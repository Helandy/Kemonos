package su.afk.kemonos.posts.presenter.pageHashLookup

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.R
import su.afk.kemonos.posts.presenter.pageHashLookup.HashLookupState.*
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.uiUtils.size.formatBytes
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HashLookupScreen(
    state: State,
    effect: Flow<Effect>,
    site: SelectedSite,
    siteSwitching: Boolean,
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val posts = state.posts.collectAsLazyPagingItems()
    val isBusy = state.isLoading || siteSwitching

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        coroutineScope.launch {
            val hash = withContext(Dispatchers.IO) {
                sha256FromUri(context, uri)
            } ?: return@launch

            onEvent(
                Event.FileHashDetected(
                    fileName = resolveDisplayName(context, uri),
                    hash = hash,
                )
            )
        }
    }

    BaseScreen(
        isScroll = false,
        topBarWindowInsets = WindowInsets(0),
        topBarScroll = TopBarScroll.None,
        contentPadding = PaddingValues(horizontal = 12.dp),
        floatingActionButtonStart = {
            SiteToggleFab(
                enable = !isBusy,
                selectedSite = site,
                onToggleSite = { onEvent(Event.SwitchSite) },
            )
        },
        topBar = {
            Text(
                text = stringResource(R.string.hash_lookup_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth(),
                onClick = { filePickerLauncher.launch("*/*") },
            ) {
                Text(stringResource(R.string.hash_lookup_search_files))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.hash_lookup_selected_file,
                    state.selectedFileName ?: "-"
                ),
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.hashInput,
                onValueChange = { onEvent(Event.HashChanged(it)) },
                label = { Text(stringResource(R.string.hash_lookup_sha256_label)) },
                singleLine = true,
                enabled = !isBusy,
                isError = state.isHashInvalid,
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.isHashInvalid) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.hash_lookup_invalid_hash),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(Event.Submit) },
            ) {
                Text(stringResource(R.string.hash_lookup_submit))
            }
        }
    ) {
        if (state.isLoading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator()
        }

        state.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        state.result?.let { result ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.hash_lookup_result_hash, result.hash),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(R.string.hash_lookup_result_id, result.id.toString()),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(
                    R.string.hash_lookup_result_meta,
                    result.mime ?: "-",
                    result.ext ?: "-",
                    result.size?.let {
                        formatBytes(it)
                    } ?: "-",
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(
                    R.string.hash_lookup_result_posts_count,
                    result.posts.size.toString()
                ),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostsContentPaging(
                uiSettingModel = state.uiSettingModel,
                postsViewMode = state.uiSettingModel.searchPostsViewMode,
                posts = posts,
                currentTag = null,
                onPostClick = { onEvent(Event.NavigateToPost(it)) },
                onRetry = { onEvent(Event.Submit) },
            )

            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

private fun resolveDisplayName(context: Context, uri: Uri): String? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            cursor.getString(nameIndex)
        } else {
            null
        }
    }
}

private fun sha256FromUri(context: Context, uri: Uri): String? {
    return context.contentResolver.openInputStream(uri)?.use { input ->
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        while (true) {
            val read = input.read(buffer)
            if (read == -1) break
            digest.update(buffer, 0, read)
        }

        digest.digest().joinToString(separator = "") { byte ->
            "%02x".format(byte)
        }
    }
}
