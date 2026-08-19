package su.afk.kemonos.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import su.afk.kemonos.auth.domain.repository.AuthRepository
import su.afk.kemonos.domain.SelectedSite
import javax.inject.Inject

/** Авторизован ли пользователь на конкретном источнике. */
class IsAuthSiteUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(site: SelectedSite): Flow<Boolean> =
        authRepository.authState.map { it.isAuthorized(site) }
}
