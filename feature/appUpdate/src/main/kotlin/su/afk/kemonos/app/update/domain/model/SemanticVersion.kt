package su.afk.kemonos.app.update.domain.model

/**
 * Модель семантической версии приложения.
 *
 * Пример:
 * "1.6.2" → SemanticVersion(major = 1, minor = 6, patch = 2)
 */
internal data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
) {
    companion object {
        /**
         * Преобразует строку версии в SemanticVersion.
         *
         * Поддерживаемые форматы:
         * "1"        → SemanticVersion(1, 0, 0)
         * "1.6"      → SemanticVersion(1, 6, 0)
         * "1.6.2"    → SemanticVersion(1, 6, 2)
         *
         * Если строка не соответствует формату SemanticVersion — вернёт null.
         */
        internal fun String.toSemVerOrNull(): SemanticVersion? {
            val parts = split(".")
            if (parts.isEmpty() || parts.size > 3) return null

            val major = parts.getOrNull(0)?.toIntOrNull() ?: return null
            val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

            return SemanticVersion(major, minor, patch)
        }

        /**
         * Сравнивает две версии и возвращает true,
         * если текущая версия новее переданной.
         *
         * Пример:
         * SemanticVersion(1, 7, 0).isNewerThan(SemVer(1, 6, 9)) → true
         * SemanticVersion(1, 6, 0).isNewerThan(SemVer(1, 6, 0)) → false
         */
        internal fun SemanticVersion.isNewerThan(currentVersion: SemanticVersion): Boolean = when {
            major != currentVersion.major -> major > currentVersion.major
            minor != currentVersion.minor -> minor > currentVersion.minor
            else -> patch > currentVersion.patch
        }
    }
}