package su.afk.kemonos.main.presenter.view

import androidx.compose.runtime.Composable
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.common.presenter.updateBanner.UpdateBanner

@Composable
internal fun MainUpdateBanner(
    info: AppUpdateInfo,
    onUpdateClick: () -> Unit,
    onLaterClick: () -> Unit,
) {
    UpdateBanner(
        info = info,
        onUpdateClick = onUpdateClick,
        onLaterClick = onLaterClick
    )
}