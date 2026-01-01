package su.afk.kemonos.main.presenter.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import su.afk.kemonos.common.R

@Composable
internal fun MainSuccess() {
    Text(stringResource(R.string.main_checking_api_success))
}