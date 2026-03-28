package su.afk.kemonos.setting.domain.model

data class TranslateModelInfo(
    val id: String,
    val sourceLanguageTag: String,
    val targetLanguageTag: String,
    val sizeBytes: Long,
)
