package su.afk.kemonos.ui.translate

interface TextTranslator {
    suspend fun translateAuto(
        text: String,
        targetLangTag: String,
    ): String
}