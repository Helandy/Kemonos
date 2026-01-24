package su.afk.kemonos.main.presenter.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.common.view.updateBanner.UpdateBanner

@Composable
internal fun MainUpdateBanner(
    info: AppUpdateInfo,
    onUpdateClick: () -> Unit,
    onLaterClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UpdateBanner(
                info = info,
                onUpdateClick = onUpdateClick,
                onLaterClick = onLaterClick
            )
        }
    }
}