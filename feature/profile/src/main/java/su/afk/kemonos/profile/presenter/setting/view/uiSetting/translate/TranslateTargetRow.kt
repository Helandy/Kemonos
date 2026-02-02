package su.afk.kemonos.profile.presenter.setting.view.uiSetting.translate

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.TranslateTarget
import su.afk.kemonos.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TranslateTargetRow(
    title: String,
    value: TranslateTarget,
    onChange: (TranslateTarget) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = value == TranslateTarget.APP,
                onClick = { onChange(TranslateTarget.APP) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                label = { Text(stringResource(R.string.settings_translate_app)) },
            )
            SegmentedButton(
                selected = value == TranslateTarget.GOOGLE,
                onClick = { onChange(TranslateTarget.GOOGLE) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                label = { Text(stringResource(R.string.settings_translate_google)) },
            )
        }
    }
    Divider()
}
