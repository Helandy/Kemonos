package su.afk.kemonos.setting.presenter.view.language

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun AppLanguageSettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    HorizontalDivider()
}

internal fun Context.openAppLanguageSettingsSafely() {
    val candidates = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(
                Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                    putExtra("android.provider.extra.APP_PACKAGE", packageName)
                }
            )
        }
        add(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        )
        add(Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS))
    }

    val pm = packageManager
    candidates.forEach { intent ->
        if (intent.resolveActivity(pm) != null) {
            val started = runCatching { startActivity(intent) }.isSuccess
            if (started) return
        }
    }
}

internal fun Context.openAppDeepLinksSettingsSafely() {
    val candidates = buildList {
        add(
            Intent("android.settings.APP_OPEN_BY_DEFAULT_SETTINGS").apply {
                data = Uri.fromParts("package", packageName, null)
                putExtra("android.provider.extra.APP_PACKAGE", packageName)
            }
        )
        add(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        )
        add(Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS))
    }

    val pm = packageManager
    candidates.forEach { intent ->
        if (intent.resolveActivity(pm) != null) {
            val started = runCatching { startActivity(intent) }.isSuccess
            if (started) return
        }
    }
}
