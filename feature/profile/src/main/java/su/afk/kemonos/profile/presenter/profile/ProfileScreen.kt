package su.afk.kemonos.profile.presenter.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.profile.views.BottomLinksBlock
import su.afk.kemonos.profile.presenter.profile.views.FavoritesCard
import su.afk.kemonos.profile.presenter.profile.views.SiteAccountCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    viewModel: ProfileViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    BaseScreen(
        isScroll = false,
        isLoading = state.isLoading,
        contentModifier = Modifier.padding(horizontal = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.SemiBold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SiteAccountCard(
                        title = stringResource(R.string.profile_coomer_account_title),
                        isLoggedIn = state.isLoginCoomer,
                        login = state.coomerLogin,
                        onLoginClick = { viewModel.onLoginClick(SelectedSite.C) },
                        onLogoutClick = { viewModel.onLogoutClick(SelectedSite.C) },
                    )

                    if (state.isLoginCoomer) {
                        FavoritesCard(
                            titleId = R.string.profile_favorites_title_coomer,
                            onFavoriteProfiles = { viewModel.onFavoriteProfilesNavigate(SelectedSite.C) },
                            onFavoritePosts = { viewModel.onFavoritePostNavigate(SelectedSite.C) }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(8.dp))

                    SiteAccountCard(
                        title = stringResource(R.string.profile_kemono_account_title),
                        isLoggedIn = state.isLoginKemono,
                        login = state.kemonoLogin,
                        onLoginClick = { viewModel.onLoginClick(SelectedSite.K) },
                        onLogoutClick = { viewModel.onLogoutClick(SelectedSite.K) },
                    )

                    if (state.isLoginKemono) {
                        FavoritesCard(
                            titleId = R.string.profile_favorites_title_kemono,
                            onFavoriteProfiles = { viewModel.onFavoriteProfilesNavigate(SelectedSite.K) },
                            onFavoritePosts = { viewModel.onFavoritePostNavigate(SelectedSite.K) }
                        )
                    }
                }

                BottomLinksBlock(
                    kemonoUrl = state.kemonoUrl,
                    coomerUrl = state.coomerUrl,
                    appVersion = state.appVersion,
                    onGitHubClick = { uriHandler.openUri("https://github.com/Helandy/Kemonos") }
                )
            }
        }
    }
}