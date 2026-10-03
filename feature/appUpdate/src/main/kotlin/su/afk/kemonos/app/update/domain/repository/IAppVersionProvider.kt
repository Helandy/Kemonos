package su.afk.kemonos.app.update.domain.repository

internal interface IAppVersionProvider {
    /** Версия установленного приложения (versionName), пустая строка если не определена. */
    fun currentVersionName(): String
}
