package su.afk.kemonos.profile.presenter.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.api.model.Login
import su.afk.kemonos.profile.presenter.profile.ProfileState.Event
import su.afk.kemonos.profile.presenter.profile.ProfileState.State
import su.afk.kemonos.profile.presenter.profile.views.*
import su.afk.kemonos.ui.date.toUiDateTime
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.preview.KemonosPreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    state: State,
    onEvent: (Event) -> Unit,
    effect: Flow<ProfileState.Effect>,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    if (pagerState.currentPage == 0) SelectedSite.C else SelectedSite.K

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        onEvent(Event.SaveExportToFolder(uri))
    }

    val importFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        onEvent(Event.ImportFavoritesFromFile(uri))
    }

    LaunchedEffect(effect) {
        effect.collect { incoming ->
            when (incoming) {
                ProfileState.Effect.OpenExportFolderPicker -> folderPickerLauncher.launch(null)
                ProfileState.Effect.OpenImportFilePicker ->
                    importFilePickerLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                is ProfileState.Effect.ShowMessage ->
                    Toast.makeText(context, incoming.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    BaseScreen(
        isScroll = true,
        isLoading = state.isLoading,
        contentModifier = Modifier.padding(horizontal = 12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 2.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SecondaryTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                            text = { Text(text = stringResource(R.string.coomer), fontWeight = FontWeight.Medium) },
                        )
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                            text = { Text(text = stringResource(R.string.kemono), fontWeight = FontWeight.Medium) },
                        )
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        pageSpacing = 12.dp,
                    ) { page ->
                        when (page) {
                            0 -> SitePage(
                                dateMode = state.uiSettingModel.dateFormatMode,
                                title = stringResource(R.string.profile_coomer_account_title),
                                isLoggedIn = state.isLoginCoomer,
                                login = state.coomerLogin,
                                site = SelectedSite.C,
                                updatedFavoritesCount = state.coomerUpdatedFavoritesCount,
                                onLoginClick = { onEvent(Event.LoginClick(SelectedSite.C)) },
                                onLogoutClick = { onEvent(Event.LogoutClick(SelectedSite.C)) },
                                onFavoriteProfiles = {
                                    onEvent(Event.FavoriteProfilesNavigate(SelectedSite.C))
                                },
                                onFavoritePosts = {
                                    onEvent(Event.FavoritePostNavigate(SelectedSite.C))
                                },
                                isExportInProgress = state.isExportInProgress,
                                isImportInProgress = state.isImportInProgress,
                                onExportFavoriteArtists = {
                                    onEvent(Event.ExportFavorites(SelectedSite.C, ProfileState.ExportType.ARTISTS))
                                },
                                onExportFavoritePosts = {
                                    onEvent(Event.ExportFavorites(SelectedSite.C, ProfileState.ExportType.POSTS))
                                },
                                onImportFavoriteArtists = {
                                    onEvent(Event.ImportFavorites(SelectedSite.C, ProfileState.ExportType.ARTISTS))
                                },
                                onImportFavoritePosts = {
                                    onEvent(Event.ImportFavorites(SelectedSite.C, ProfileState.ExportType.POSTS))
                                },
                            )

                            1 -> SitePage(
                                dateMode = state.uiSettingModel.dateFormatMode,
                                title = stringResource(R.string.profile_kemono_account_title),
                                isLoggedIn = state.isLoginKemono,
                                login = state.kemonoLogin,
                                site = SelectedSite.K,
                                updatedFavoritesCount = state.kemonoUpdatedFavoritesCount,
                                onLoginClick = { onEvent(Event.LoginClick(SelectedSite.K)) },
                                onLogoutClick = { onEvent(Event.LogoutClick(SelectedSite.K)) },
                                onFavoriteProfiles = {
                                    onEvent(Event.FavoriteProfilesNavigate(SelectedSite.K))
                                },
                                onFavoritePosts = {
                                    onEvent(Event.FavoritePostNavigate(SelectedSite.K))
                                },
                                isExportInProgress = state.isExportInProgress,
                                isImportInProgress = state.isImportInProgress,
                                onExportFavoriteArtists = {
                                    onEvent(Event.ExportFavorites(SelectedSite.K, ProfileState.ExportType.ARTISTS))
                                },
                                onExportFavoritePosts = {
                                    onEvent(Event.ExportFavorites(SelectedSite.K, ProfileState.ExportType.POSTS))
                                },
                                onImportFavoriteArtists = {
                                    onEvent(Event.ImportFavorites(SelectedSite.K, ProfileState.ExportType.ARTISTS))
                                },
                                onImportFavoritePosts = {
                                    onEvent(Event.ImportFavorites(SelectedSite.K, ProfileState.ExportType.POSTS))
                                },
                            )
                        }
                    }
                }
            }

            DownloadsButton(onClick = { onEvent(Event.NavigateToDownloads) })
            AuthorsBlacklistButton(onClick = { onEvent(Event.NavigateToAuthorsBlacklist) })
            SettingsButton(onClick = { onEvent(Event.NavigateToSettings) })
            FaqButton(onClick = { onEvent(Event.NavigateToFaq) })
            Spacer(Modifier.height(8.dp))
        }

        if (state.showLogoutConfirm) {
            LogoutDialog(
                site = state.logoutSite,
                onConfirm = { onEvent(Event.LogoutConfirm) },
                onDismiss = { onEvent(Event.LogoutDismiss) }
            )
        }
    }
}

@Composable
private fun ProfileSummaryCard(
    state: State,
    selectedSite: SelectedSite,
) {
    val selectedLogin = when (selectedSite) {
        SelectedSite.K -> state.kemonoLogin
        SelectedSite.C -> state.coomerLogin
    }
    val selectedName = selectedLogin?.username
    val joinedText = selectedLogin?.createdAt
        ?.toUiDateTime(state.uiSettingModel.dateFormatMode)
        ?.let { stringResource(R.string.profile_joined_role, it) }
    val secondaryText = selectedName?.takeIf { it.isNotBlank() }
        ?.let { stringResource(R.string.profile_hello, it) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            if (!secondaryText.isNullOrBlank()) {
                Text(
                    text = secondaryText,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = " ",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                )
            }
            if (!joinedText.isNullOrBlank()) {
                Text(
                    text = joinedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = stringResource(R.string.profile_not_logged_in_message),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(name = "ProfileScreen")
@Composable
private fun PreviewProfileScreen() {
    KemonosPreviewScreen {
        ProfileScreen(
            state = State(
                isLoading = false,
                isLoginKemono = true,
                isLoginCoomer = true,
                isLogin = true,
                kemonoLogin = Login(
                    id = 1,
                    username = "KemonoUser",
                    createdAt = "2026-02-25T10:40:00Z",
                    role = "user",
                ),
                coomerLogin = Login(
                    id = 2,
                    username = "CoomerUser",
                    createdAt = "2026-02-20T09:00:00Z",
                    role = "user",
                ),
                kemonoUpdatedFavoritesCount = 12,
                coomerUpdatedFavoritesCount = 3,
            ),
            onEvent = {},
            effect = emptyFlow(),
        )
    }
}
