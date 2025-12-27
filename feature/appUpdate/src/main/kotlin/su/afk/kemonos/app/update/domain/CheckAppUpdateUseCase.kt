package su.afk.kemonos.app.update.domain

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.app.update.api.useCase.ICheckAppUpdateUseCase
import su.afk.kemonos.app.update.data.IAppUpdateRepository
import su.afk.kemonos.app.update.util.currentVersionName
import su.afk.kemonos.app.update.util.isNewerThan
import su.afk.kemonos.app.update.util.toSemVerOrNull
import javax.inject.Inject

internal class CheckAppUpdateUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: IAppUpdateRepository,
) : ICheckAppUpdateUseCase {

    override suspend operator fun invoke(): AppUpdateInfo? = runCatching {
        val current = context.currentVersionName().toSemVerOrNull() ?: return null

        val remote = repository.getLatestVersion().getOrNull() ?: return null

        val latestTag = remote.latestVersionName.removePrefix("v")
        val latest = latestTag.toSemVerOrNull() ?: return null

        if (latest.isNewerThan(current)) remote else null
    }.getOrNull()
}