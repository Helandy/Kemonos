package su.afk.kemonos.profile.presenter.profile.delegate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.auth.ClearAuthUseCase
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.presenter.profile.ProfileState
import javax.inject.Inject

internal class LogoutDelegate @Inject constructor(
    private val clearAuthUseCase: ClearAuthUseCase,
) {
    fun onLogoutClick(
        site: SelectedSite,
        updateState: (ProfileState.() -> ProfileState) -> Unit,
    ) {
        updateState {
            copy(
                showLogoutConfirm = true,
                logoutSite = site
            )
        }
    }

    fun onLogoutConfirm(
        scope: CoroutineScope,
        getState: () -> ProfileState,
        updateState: (ProfileState.() -> ProfileState) -> Unit,
    ) {
        scope.launch {
            val site = getState().logoutSite ?: return@launch
            clearAuthUseCase(site)

            updateState {
                copy(
                    showLogoutConfirm = false,
                    logoutSite = null
                )
            }
        }
    }

    fun onLogoutDismiss(
        updateState: (ProfileState.() -> ProfileState) -> Unit,
    ) {
        updateState {
            copy(
                showLogoutConfirm = false,
                logoutSite = null
            )
        }
    }
}