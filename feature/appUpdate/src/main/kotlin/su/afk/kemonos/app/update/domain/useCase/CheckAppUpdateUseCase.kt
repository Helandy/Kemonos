package su.afk.kemonos.app.update.domain.useCase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.app.update.api.useCase.ICheckAppUpdateUseCase
import su.afk.kemonos.app.update.domain.model.SemanticVersion.Companion.isNewerThan
import su.afk.kemonos.app.update.domain.model.SemanticVersion.Companion.toSemVerOrNull
import su.afk.kemonos.app.update.domain.repository.IAppUpdateRepository
import su.afk.kemonos.app.update.util.currentVersionName
import javax.inject.Inject

/**
 * Проверяет наличие обновления приложения.
 *
 * Алгоритм:
 * 1. Берёт текущую версию приложения
 * 2. Получает последнюю версию с сервера
 * 3. Сравнивает версии
 *
 * Если серверная версия новее — возвращает AppUpdateInfo,
 * иначе возвращает null.
 */
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