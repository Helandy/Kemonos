package su.afk.kemonos.common.translate

interface TextTranslator {
    suspend fun translateAuto(
        text: String,
        targetLangTag: String,
    ): String
}