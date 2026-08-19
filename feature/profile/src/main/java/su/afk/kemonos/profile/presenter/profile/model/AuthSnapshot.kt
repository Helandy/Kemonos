package su.afk.kemonos.profile.presenter.profile.model

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.api.model.Login

data class AuthSnapshot(
    val authorizedSites: Set<SelectedSite>,
    val logins: Map<SelectedSite, Login>,
    val updatedFavoritesCounts: Map<SelectedSite, Int>,
)
