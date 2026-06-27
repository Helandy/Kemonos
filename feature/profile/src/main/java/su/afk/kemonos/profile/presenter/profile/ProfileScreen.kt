package su.afk.kemonos.profile.presenter.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    val ui = state.uiSettingModel
    val visibleSites = ui.enabledSiteList
    val pageCount = visibleSites.size
    val initialPage = visibleSites.indexOf(ui.siteDisplayMode.defaultSite)
        .takeIf { it >= 0 }
        ?: 0

    var savedPage by rememberSaveable(pageCount) {
        mutableIntStateOf(initialPage.coerceIn(0, pageCount - 1))
    }
    val pagerState = rememberPagerState(
        initialPage = savedPage.coerceIn(0, pageCount - 1),
        pageCount = { pageCount }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage, pageCount) {
        savedPage = pagerState.currentPage.coerceIn(0, pageCount - 1)
    }

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
                        selectedTabIndex = pagerState.currentPage.coerceIn(0, pageCount - 1),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        visibleSites.forEachIndexed { index, site ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = { Text(text = site.tabTitle(), fontWeight = FontWeight.Medium) },
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        pageSpacing = 12.dp,
                    ) { page ->
                        val site = visibleSites[page]
                        SitePage(
                            dateMode = state.uiSettingModel.dateFormatMode,
                            title = site.accountTitle(),
                            isLoggedIn = state.isLoggedIn(site),
                            login = state.login(site),
                            site = site,
                            updatedFavoritesCount = state.updatedFavoritesCount(site),
                            onLoginClick = { onEvent(Event.LoginClick(site)) },
                            onLogoutClick = { onEvent(Event.LogoutClick(site)) },
                            onFavoriteProfiles = { onEvent(Event.FavoriteProfilesNavigate(site)) },
                            onFavoritePosts = { onEvent(Event.FavoritePostNavigate(site)) },
                            isExportInProgress = state.isExportInProgress,
                            isImportInProgress = state.isImportInProgress,
                            onExportFavoriteArtists = {
                                onEvent(Event.ExportFavorites(site, ProfileState.ExportType.ARTISTS))
                            },
                            onExportFavoritePosts = {
                                onEvent(Event.ExportFavorites(site, ProfileState.ExportType.POSTS))
                            },
                            onImportFavoriteArtists = {
                                onEvent(Event.ImportFavorites(site, ProfileState.ExportType.ARTISTS))
                            },
                            onImportFavoritePosts = {
                                onEvent(Event.ImportFavorites(site, ProfileState.ExportType.POSTS))
                            },
                        )
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
        SelectedSite.P -> state.pawchiveLogin
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

@Composable
private fun SelectedSite.tabTitle(): String = when (this) {
    SelectedSite.K -> stringResource(R.string.kemono)
    SelectedSite.C -> stringResource(R.string.coomer)
    SelectedSite.P -> stringResource(R.string.pawchive)
}

@Composable
private fun SelectedSite.accountTitle(): String = when (this) {
    SelectedSite.K -> stringResource(R.string.profile_kemono_account_title)
    SelectedSite.C -> stringResource(R.string.profile_coomer_account_title)
    SelectedSite.P -> stringResource(R.string.profile_pawchive_account_title)
}

private fun State.isLoggedIn(site: SelectedSite): Boolean = when (site) {
    SelectedSite.K -> isLoginKemono
    SelectedSite.C -> isLoginCoomer
    SelectedSite.P -> isLoginPawchive
}

private fun State.login(site: SelectedSite): Login? = when (site) {
    SelectedSite.K -> kemonoLogin
    SelectedSite.C -> coomerLogin
    SelectedSite.P -> pawchiveLogin
}

private fun State.updatedFavoritesCount(site: SelectedSite): Int = when (site) {
    SelectedSite.K -> kemonoUpdatedFavoritesCount
    SelectedSite.C -> coomerUpdatedFavoritesCount
    SelectedSite.P -> pawchiveUpdatedFavoritesCount
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
                isLoginPawchive = false,
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
                pawchiveUpdatedFavoritesCount = 0,
            ),
            onEvent = {},
            effect = emptyFlow(),
        )
    }
}
