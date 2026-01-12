package su.afk.kemonos.common.translate

import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.Locale as JLocale

internal class MlKitTextTranslator(
    targetLang: String? = null,
) : TextTranslator {

    private val target: String = targetLang ?: TranslateLanguage.fromLanguageTag(JLocale.getDefault().language)
    ?: TranslateLanguage.ENGLISH

    /**
     * Кешируем Translator по паре source->target, чтобы:
     * - не пересоздавать постоянно
     * - не дергать downloadModelIfNeeded каждый раз
     *
     * Важно: Translator надо закрывать, но в singleton-подходе можно держать до конца жизни приложения.
     */
    private val translators = ConcurrentHashMap<String, Translator>()

    override suspend fun translateAuto(text: String): String = withContext(Dispatchers.IO) {
        val cleaned = text.preprocessForTranslation()
        if (cleaned.isBlank()) return@withContext ""

        val source = detectLanguageSafe(cleaned)
        if (source == target) return@withContext cleaned

        val key = "$source->$target"
        val translator = translators.getOrPut(key) { createTranslator(source, target) }

        Tasks.await(translator.downloadModelIfNeeded(DownloadConditions.Builder().build()))

        val chunks = cleaned.chunkForTranslation(maxLen = 900)
        val translatedChunks = chunks.map { Tasks.await(translator.translate(it)) }
        translatedChunks.joinToString(separator = "")
    }

    private fun createTranslator(sourceLang: String, targetLang: String): Translator {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()
        return Translation.getClient(options)
    }

    private suspend fun detectLanguageSafe(text: String): String = withContext(Dispatchers.IO) {
        val identifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(0.35f)
                .build()
        )
        try {
            val lang = Tasks.await(identifier.identifyLanguage(text))
            when {
                lang == "und" -> guessFallback(text)
                TranslateLanguage.fromLanguageTag(lang) == null -> guessFallback(text)
                else -> lang
            }
        } finally {
            identifier.close()
        }
    }

    private fun guessFallback(text: String): String {
        // простая эвристика для смешанных текстов
        val hasCyrillic = text.any { it in 'А'..'я' || it == 'ё' || it == 'Ё' }
        return if (hasCyrillic) TranslateLanguage.RUSSIAN else TranslateLanguage.ENGLISH
    }
}

/** Чистка текста перед переводом */
private fun String.preprocessForTranslation(): String {
    return this
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("</p\\s*>", RegexOption.IGNORE_CASE), "\n\n")
        .replace(Regex("<[^>]*>"), " ")
        // выкинуть URL
        .replace(Regex("https?://\\S+"), "")
        // нормализовать пробелы, но сохранить переносы
        .replace(Regex("[ \\t\\x0B\\f\\r]+"), " ")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
}

/** Разбить на чанки, стараясь резать по предложениям/переносам */
private fun String.chunkForTranslation(maxLen: Int): List<String> {
    if (length <= maxLen) return listOf(this)

    val out = ArrayList<String>()
    var i = 0
    while (i < length) {
        val end = (i + maxLen).coerceAtMost(length)
        val slice = substring(i, end)

        // пытаемся найти хорошую границу
        val cutAt = maxOf(
            slice.lastIndexOf("\n\n"),
            slice.lastIndexOf("\n"),
            slice.lastIndexOf(". "),
            slice.lastIndexOf("! "),
            slice.lastIndexOf("? "),
        ).takeIf { it >= maxLen * 0.6 } ?: -1

        val realEnd = if (cutAt != -1) i + cutAt + 1 else end
        out.add(substring(i, realEnd))
        i = realEnd
    }
    return out
}