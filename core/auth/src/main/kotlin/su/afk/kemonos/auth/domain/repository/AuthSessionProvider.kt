package su.afk.kemonos.auth.domain.repository

import su.afk.kemonos.domain.SelectedSite

interface AuthSessionProvider {
    suspend fun getSession(site: SelectedSite): String?
}
