package su.afk.kemonos.setting.presenter.view.translate

import java.util.*

internal fun displayTranslateLanguage(languageTag: String): String {
    val normalized = languageTag.replace('_', '-')
    val locale = Locale.forLanguageTag(normalized)
    val displayName = locale.getDisplayName(locale).trim()
    if (displayName.isNotBlank()) return displayName.replaceFirstChar { it.titlecase(locale) }
    return languageTag
}
