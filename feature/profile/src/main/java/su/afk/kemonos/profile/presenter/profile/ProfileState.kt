package su.afk.kemonos.profile.presenter.profile

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.ui.UiSettingModel
import su.afk.kemonos.profile.api.model.Login

internal data class ProfileState(
    val isLoading: Boolean = true,

    val isLoginKemono: Boolean = false,
    val isLoginCoomer: Boolean = false,
    val isLogin: Boolean = false,

    val kemonoLogin: Login? = null,
    val coomerLogin: Login? = null,

    val showLogoutConfirm: Boolean = false,
    val logoutSite: SelectedSite? = null,

    val kemonoUpdatedFavoritesCount: Int = 0,
    val coomerUpdatedFavoritesCount: Int = 0,

    val uiSettingModel: UiSettingModel = UiSettingModel(),
)