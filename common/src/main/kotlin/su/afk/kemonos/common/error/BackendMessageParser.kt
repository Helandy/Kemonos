package su.afk.kemonos.common.error

import kotlinx.serialization.json.*
import javax.inject.Inject

/**
 * Парсер человекочитаемого текста ошибки из backend errorBody.
 *
 * Поддерживает:
 * 1) HTML-страницы (например "Access Denied" от прокси/Cloudflare/гео-блокировки):
 *    - вытащит <h1> и первый <p>, очистит теги.
 *
 * 2) JSON-ошибки:
 *    - попробует найти типовые ключи message/error/detail/description/title/reason
 *    - поддержит {"errors":[...]} / {"error":{...}} / {"details":...}
 *    - если структура неизвестна — попробует найти первую строку где угодно в JSON.
 *
 * 3) Plain text:
 *    - если не HTML и не JSON — вернёт trimmed текст как есть.
 *
 * Важно: Json берём из DI, чтобы настройки (lenient/ignoreUnknownKeys/explicitNulls)
 * были едиными по всему приложению.
 */
class BackendMessageParser @Inject constructor(
    private val json: Json,
) {

    /**
     * Главный метод.
     * @return человекочитаемое сообщение или null (если тело пустое/бессмысленное)
     */
    fun extract(body: String?): String? {
        val trimmed = body?.trim().orEmpty()
        if (trimmed.isEmpty()) return null

        /** Частый кейс: backend/edge возвращает HTML вместо JSON */
        if (looksLikeHtml(trimmed)) return extractFromHtml(trimmed)

        /** Пытаемся распарсить JSON (без regex, чтобы не ломаться на экранировании и вложенности) */
        val fromJson = runCatching { extractFromJson(trimmed) }.getOrNull()

        /** Если JSON не распарсили — fallback на plain text */
        return (fromJson ?: trimmed)
            .normalizeOneLine()
            .takeIf { it.isNotBlank() }
    }

    /**
     * Парсинг JSON тела. Возвращаем первое осмысленное сообщение.
     */
    private fun extractFromJson(body: String): String? {
        val el = json.parseToJsonElement(body)

        /** Типовые ключи с текстом ошибки */
        val keys = listOf("message", "error", "detail", "description", "title", "reason")

        /** 1) Сначала ищем ключи по приоритету (включая вложенные объекты) */
        /** 2) Потом частые структуры errors/error/details */
        /** 3) И как последний шанс — любую первую строку в дереве JSON */
        return findFirstByKeys(el, keys)
            ?: findFirstStringInErrors(el)
            ?: findFirstStringAnywhere(el)
    }

    /**
     * Ищем значения по ключам (message/error/detail/...) в объекте,
     * затем рекурсивно проваливаемся глубже (вложенные объекты/массивы).
     */
    private fun findFirstByKeys(el: JsonElement, keys: List<String>): String? = when (el) {
        is JsonObject -> {
            /** 1) Прямой поиск ключей в текущем объекте */
            keys.firstNotNullOfOrNull { k ->
                el[k]?.let(::jsonElementToString)?.takeIf { it.isNotBlank() }
            }
            /** 2) Если не нашли — рекурсивно по всем значениям объекта */
                ?: el.values.firstNotNullOfOrNull { v -> findFirstByKeys(v, keys) }
        }

        is JsonArray -> {
            /** Для массива — ищем первое подходящее значение в элементах */
            el.firstNotNullOfOrNull { v -> findFirstByKeys(v, keys) }
        }

        else -> null
    }

    /**
     * Поддержка популярных форматов:
     * - {"errors":["..."]}
     * - {"errors":[{"message":"..."}]}
     * - {"error":{"message":"..."}}
     * - {"details":"..."}
     */
    private fun findFirstStringInErrors(el: JsonElement): String? {
        if (el !is JsonObject) return null
        val node = el["errors"] ?: el["error"] ?: el["details"] ?: return null
        return jsonElementToString(node)?.takeIf { it.isNotBlank() }
    }

    /**
     * Последний шанс: найти вообще первую строку в JSON-дереве.
     * Полезно, если структура неожиданная.
     */
    private fun findFirstStringAnywhere(el: JsonElement): String? = when (el) {
        is JsonPrimitive -> if (el.isString) el.content else null
        is JsonObject -> el.values.firstNotNullOfOrNull(::findFirstStringAnywhere)
        is JsonArray -> el.firstNotNullOfOrNull(::findFirstStringAnywhere)
        else -> null
    }

    /**
     * Преобразуем JsonElement в строку:
     * - primitive string -> content
     * - array -> первый элемент
     * - object -> пробуем message/detail/error внутри (частый случай)
     */
    private fun jsonElementToString(el: JsonElement): String? = when (el) {
        is JsonPrimitive -> if (el.isString) el.content else el.toString()
        is JsonArray -> el.firstOrNull()?.let(::jsonElementToString)
        is JsonObject -> el["message"]?.let(::jsonElementToString)
            ?: el["detail"]?.let(::jsonElementToString)
            ?: el["error"]?.let(::jsonElementToString)

        else -> null
    }

    /** ---------------- HTML part ---------------- */

    /** Очень простой санитайзер: снимаем теги, приводим пробелы */
    private val HTML_TAG = Regex("<[^>]+>")

    /** Достаём заголовок и первый параграф (обычно достаточно для “Access Denied” страниц) */
    private val H1 = Regex("(?is)<h1[^>]*>(.*?)</h1>")
    private val P = Regex("(?is)<p[^>]*>(.*?)</p>")

    /**
     * Быстрая эвристика "это похоже на HTML".
     * Мы не парсим полноценно HTML, только определяем что это не JSON/plain text.
     */
    private fun looksLikeHtml(s: String): Boolean {
        val lower = s.take(200).lowercase()
        return lower.startsWith("<!doctype") ||
                lower.startsWith("<html") ||
                lower.contains("<body") ||
                lower.contains("<h1") ||
                lower.contains("<p>")
    }

    /**
     * Из HTML вытаскиваем "смысл": <h1> и первый <p>,
     * декодим несколько html-entities, выкидываем теги.
     */
    private fun extractFromHtml(html: String): String? {
        fun stripTags(x: String) = x
            /** минимальный decode entities (достаточно для простых страниц) */
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            /** remove tags + normalize spaces */
            .replace(HTML_TAG, " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        val h1 = H1.find(html)?.groupValues?.getOrNull(1)?.let(::stripTags)
        val p1 = P.find(html)?.groupValues?.getOrNull(1)?.let(::stripTags)

        /** Компактный формат: "Access Denied — Due to RKN rules ..." */
        return listOfNotNull(h1?.takeIf { it.isNotBlank() }, p1?.takeIf { it.isNotBlank() })
            .joinToString(" — ")
            .trim()
            .takeIf { it.isNotBlank() }
    }

    /**
     * Нормализуем переносы/табы/двойные пробелы, чтобы UI не выглядел грязно.
     */
    private fun String.normalizeOneLine(): String =
        replace(Regex("[\\r\\n\\t]+"), " ")
            .replace(Regex("\\s{2,}"), " ")
            .trim()
}