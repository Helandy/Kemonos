package su.afk.kemonos.core.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.Login
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
) {
    operator fun invoke(): Flow<AuthState> =
        authLocalDataSource.authState
}

class IsAuthKemonoUseCase @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
) {
    operator fun invoke(): Flow<Boolean> =
        authLocalDataSource.authState.map { it.isKemonoAuthorized }
}

class IsAuthCoomerUseCase @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
) {
    operator fun invoke(): Flow<Boolean> =
        authLocalDataSource.authState.map { it.isCoomerAuthorized }
}

class SaveAuthSiteUseCase @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
) {
    suspend operator fun invoke(site: SelectedSite, session: String, user: Login) {
        authLocalDataSource.saveAuth(
            site = site,
            session = session,
            user = user
        )
    }
}

class ClearAuthUseCase @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
) {
    suspend operator fun invoke(site: SelectedSite) {
        authLocalDataSource.clearAuth(site)
    }
}

class ClearAllAuthUseCase @Inject constructor(
    private val authLocalDataSource: IAuthLocalDataSource,
) {
    suspend operator fun invoke() {
        authLocalDataSource.clearAll()
    }
}