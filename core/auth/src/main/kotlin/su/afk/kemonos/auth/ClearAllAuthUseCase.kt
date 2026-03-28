package su.afk.kemonos.auth

import su.afk.kemonos.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ClearAllAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        authRepository.clearAll()
    }
}
