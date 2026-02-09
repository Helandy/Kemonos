package su.afk.kemonos.app.update.domain.repository

import su.afk.kemonos.app.update.api.model.AppUpdateInfo

interface IAppUpdateRepository {
    suspend fun getLatestVersion(): Result<AppUpdateInfo>
}