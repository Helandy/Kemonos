package su.afk.kemonos.main.presenter.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R

@Composable
internal fun MainLoading() {
    CircularProgressIndicator()
    Spacer(Modifier.height(16.dp))
    Text(stringResource(R.string.main_checking_api))
}