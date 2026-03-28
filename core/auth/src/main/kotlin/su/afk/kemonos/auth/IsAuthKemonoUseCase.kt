package su.afk.kemonos.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.auth.domain.repository.AuthRepository
import javax.inject.Inject

class IsAuthKemonoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<Boolean> =
        authRepository.authState.map { it.isKemonoAuthorized }
}
