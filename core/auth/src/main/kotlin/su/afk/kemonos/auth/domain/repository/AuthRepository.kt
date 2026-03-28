package su.afk.kemonos.auth.domain.repository

import kotlinx.coroutines.flow.Flow
import su.afk.kemonos.auth.domain.model.AuthState
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AuthUser

interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun saveAuth(
        site: SelectedSite,
        session: String,
        user: AuthUser,
    )

    suspend fun clearAuth(site: SelectedSite)

    suspend fun clearAll()
}
