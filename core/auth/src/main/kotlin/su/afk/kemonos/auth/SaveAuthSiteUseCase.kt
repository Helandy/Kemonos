package su.afk.kemonos.auth

import su.afk.kemonos.auth.domain.repository.AuthRepository
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.AuthUser
import javax.inject.Inject

class SaveAuthSiteUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(site: SelectedSite, session: String, user: AuthUser) {
        authRepository.saveAuth(
            site = site,
            session = session,
            user = user
        )
    }
}
