package su.afk.kemonos.app.update.util

import android.content.Context

/**
 * Возвращает текущую версию приложения в виде строки.
 *
 * Пример:
 * versionName = "1.6.0" → вернёт "1.6.0"
 * Если versionName отсутствует — вернёт пустую строку.
 */
internal fun Context.currentVersionName(): String {
    val pm = packageManager
    val info = pm.getPackageInfo(packageName, 0)
    return info.versionName.orEmpty()
}