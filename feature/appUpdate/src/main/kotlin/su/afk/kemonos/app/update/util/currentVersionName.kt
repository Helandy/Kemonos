package su.afk.kemonos.app.update.util

import android.content.Context

fun Context.currentVersionName(): String {
    val pm = packageManager
    val info = pm.getPackageInfo(packageName, 0)
    return info.versionName.orEmpty()
}

data class SemVer(val major: Int, val minor: Int, val patch: Int)

fun String.toSemVerOrNull(): SemVer? {
    val parts = split(".")
    if (parts.isEmpty() || parts.size > 3) return null

    val major = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

    return SemVer(major, minor, patch)
}

fun SemVer.isNewerThan(other: SemVer): Boolean = when {
    major != other.major -> major > other.major
    minor != other.minor -> minor > other.minor
    else -> patch > other.patch
}