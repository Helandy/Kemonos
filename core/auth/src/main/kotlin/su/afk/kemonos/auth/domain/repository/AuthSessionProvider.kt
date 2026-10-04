package su.afk.kemonos.auth.domain.repository

import su.afk.kemonos.domain.SelectedSite

interface AuthSessionProvider {
    fun getSession(site: SelectedSite): String?
}
