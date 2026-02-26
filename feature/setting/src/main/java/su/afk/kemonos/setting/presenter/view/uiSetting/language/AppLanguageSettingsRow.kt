package su.afk.kemonos.setting.presenter.view.uiSetting.language

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

@Composable
internal fun AppLanguageSettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Divider()
}
