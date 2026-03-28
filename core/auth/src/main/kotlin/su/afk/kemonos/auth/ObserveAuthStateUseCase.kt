package su.afk.kemonos.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import su.afk.kemonos.auth.domain.model.AuthState
import su.afk.kemonos.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<AuthState> =
        authRepository.authState.distinctUntilChanged()
}
