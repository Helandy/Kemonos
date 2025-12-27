package su.afk.kemonos.app.update.api.model

data class AppUpdateInfo(
    val latestVersionName: String,
    val releaseUrl: String,
    val changelog: String,
)