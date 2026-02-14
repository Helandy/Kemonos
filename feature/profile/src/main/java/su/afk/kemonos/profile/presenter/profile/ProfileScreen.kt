package su.afk.kemonos.profile.presenter.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.profile.ProfileState.*
import su.afk.kemonos.profile.presenter.profile.views.DownloadsButton
import su.afk.kemonos.profile.presenter.profile.views.LogoutDialog
import su.afk.kemonos.profile.presenter.profile.views.SettingsButton
import su.afk.kemonos.profile.presenter.profile.views.SitePage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    state: State,
    effect: Flow<Effect>,
    onEvent: (Event) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    BaseScreen(
        isScroll = false,
        isLoading = state.isLoading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )

            // ---------- Tabs ----------
            TabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    },
                    text = { Text("Coomer") }
                )

                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    },
                    text = { Text("Kemono") }
                )
            }

            // ---------- Pager ----------
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                pageSpacing = 16.dp,
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
                        }
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
                        }
                    )
                }
            }

            DownloadsButton(
                onClick = { onEvent(Event.NavigateToDownloads) }
            )

            SettingsButton(
                onClick = { onEvent(Event.NavigateToSettings) }
            )
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
