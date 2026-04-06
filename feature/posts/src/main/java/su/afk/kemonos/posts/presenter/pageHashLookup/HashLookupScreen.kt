package su.afk.kemonos.posts.presenter.pageHashLookup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.R
import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain
import su.afk.kemonos.posts.presenter.pageHashLookup.HashLookupState.*
import su.afk.kemonos.preferences.ui.FabVisibilityMode
import su.afk.kemonos.ui.components.button.SiteToggleFab
import su.afk.kemonos.ui.components.posts.PostsContentPaging
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.TopBarScroll
import su.afk.kemonos.ui.uiUtils.file.resolveDisplayName
import su.afk.kemonos.ui.uiUtils.file.sha256FromUri
import su.afk.kemonos.ui.uiUtils.size.formatBytes

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
    val isEmptyResult = !isBusy && state.result?.posts?.isEmpty() == true
    val topBarScrollMode = if (isEmptyResult) TopBarScroll.Pinned else TopBarScroll.EnterAlways
    val pullState = rememberPullToRefreshState()

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
        topBarScroll = topBarScrollMode,
        contentPadding = PaddingValues(horizontal = 12.dp),
        floatingActionButtonStart = {
            if (FabVisibilityMode.shouldShowSiteToggleFab(state.uiSettingModel)) {
                SiteToggleFab(
                    enable = !isBusy,
                    selectedSite = site,
                    onToggleSite = { onEvent(Event.SwitchSite) },
                )
            }
        },
        topBar = {
            Text(
                text = stringResource(R.string.hash_lookup_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            )

            Text(
                text = stringResource(R.string.hash_lookup_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(10.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            enabled = !isBusy,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(R.string.hash_lookup_search_files))
                        }

                        Button(
                            onClick = { onEvent(Event.Submit) },
                            enabled = !isBusy,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(R.string.hash_lookup_submit))
                        }
                    }

                    if (state.selectedFileName != null) {
                        Text(
                            text = stringResource(
                                R.string.hash_lookup_selected_file,
                                state.selectedFileName
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    OutlinedTextField(
                        value = state.hashInput,
                        onValueChange = { onEvent(Event.HashChanged(it)) },
                        label = { Text(stringResource(R.string.hash_lookup_sha256_label)) },
                        singleLine = false,
                        minLines = 1,
                        maxLines = 10,
                        enabled = !isBusy,
                        isError = state.isHashInvalid,
                        placeholder = { Text(stringResource(R.string.hash_lookup_sha256_placeholder)) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (state.isHashInvalid) {
                        Text(
                            text = stringResource(R.string.hash_lookup_invalid_hash),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Text(
                        text = stringResource(
                            R.string.hash_lookup_hash_length,
                            state.hashInput.trim().length.toString()
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            state = pullState,
            isRefreshing = isBusy,
            onRefresh = { onEvent(Event.PullRefresh) },
        ) {
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.hash_lookup_error_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message.ifBlank {
                                stringResource(R.string.hash_lookup_error_empty_response)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        TextButton(
                            onClick = { onEvent(Event.Submit) },
                            enabled = !isBusy,
                        ) {
                            Text(stringResource(R.string.hash_lookup_try_again))
                        }
                    }
                }
            }

            state.result?.let { result ->
                if (result.posts.isEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HashLookupResultSummary(result = result)
                    Spacer(modifier = Modifier.height(12.dp))
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.hash_lookup_empty_title),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.hash_lookup_no_posts_for_hash),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                } else {
                    PostsContentPaging(
                        uiSettingModel = state.uiSettingModel,
                        postsViewMode = state.uiSettingModel.searchPostsViewMode,
                        gridPostsSize = state.uiSettingModel.searchPostsGridSize,
                        posts = posts,
                        currentTag = null,
                        onPostClick = { onEvent(Event.NavigateToPost(it)) },
                        onRetry = { onEvent(Event.Submit) },
                        header = {
                            Spacer(modifier = Modifier.height(12.dp))
                            HashLookupResultSummary(result = result)
                            Spacer(modifier = Modifier.height(8.dp))
                        },
                    )
                }

                Spacer(modifier = Modifier.height(88.dp))
            }
        }
    }
}

@Composable
private fun HashLookupResultSummary(result: HashLookupDomain) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.hash_lookup_result_title),
                style = MaterialTheme.typography.titleMedium,
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HashStatTile(
                    label = stringResource(R.string.hash_lookup_result_id_label),
                    value = result.id.toString(),
                    modifier = Modifier.weight(1f),
                )
                HashStatTile(
                    label = stringResource(R.string.hash_lookup_result_posts_label),
                    value = result.posts.size.toString(),
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HashStatTile(
                    label = stringResource(R.string.hash_lookup_result_mime_label),
                    value = result.mime ?: "-",
                    modifier = Modifier.weight(1f),
                )
                HashStatTile(
                    label = stringResource(R.string.hash_lookup_result_ext_label),
                    value = result.ext ?: "-",
                    modifier = Modifier.weight(1f),
                )
            }

            HashStatTile(
                label = stringResource(R.string.hash_lookup_result_size_label),
                value = result.size?.let { formatBytes(it) } ?: "-",
                modifier = Modifier.fillMaxWidth(),
            )

            HashStatTile(
                label = stringResource(R.string.hash_lookup_result_hash_label),
                value = result.hash,
                monospace = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HashStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    monospace: Boolean = false,
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default
            ),
        )
    }
}
