package su.afk.kemonos.auth

import su.afk.kemonos.auth.domain.repository.AuthRepository
import su.afk.kemonos.domain.SelectedSite
import javax.inject.Inject

class ClearAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(site: SelectedSite) {
        authRepository.clearAuth(site)
    }
}
