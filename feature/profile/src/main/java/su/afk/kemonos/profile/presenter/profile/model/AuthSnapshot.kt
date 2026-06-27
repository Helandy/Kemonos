package su.afk.kemonos.profile.presenter.profile.model

import su.afk.kemonos.profile.api.model.Login

interface data
class AuthSnapshot(
    val isKemonoAuthorized: Boolean,
    val isCoomerAuthorized: Boolean,
    val isPawchiveAuthorized: Boolean,
    val kemonoLogin: Login?,
    val coomerLogin: Login?,
    val pawchiveLogin: Login?,
    val kemonoUpdatedFavoritesCount: Int,
    val coomerUpdatedFavoritesCount: Int,
    val pawchiveUpdatedFavoritesCount: Int,
)
