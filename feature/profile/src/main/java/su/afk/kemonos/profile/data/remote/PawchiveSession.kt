package su.afk.kemonos.profile.data.remote

import com.google.gson.JsonParser
import java.util.Base64

/** Значение куки `session` из заголовков Set-Cookie. */
internal fun List<String>.pawchiveSessionCookie(): String? =
    firstOrNull { it.startsWith("session=") }
        ?.substringAfter("session=")
        ?.substringBefore(';')
        ?.takeIf { it.isNotBlank() }

/**
 * Текст ошибки, который pawchive кладёт во flash подписанной Flask-куки:
 * `{"_flashes":[{" t":["message","Username or password is incorrect"]}]}`.
 *
 * Подпись не проверяем: сообщение только показываем пользователю, решение об
 * успехе входа принимается по ответу API, а не по этой куке.
 */
internal fun String.pawchiveFlashMessage(): String? = runCatching {
    val payload = substringBefore('.')

    /** Пустой префикс означает сжатый zlib-payload — такой не разбираем. */
    if (payload.isEmpty()) return null

    val json = String(Base64.getUrlDecoder().decode(payload), Charsets.UTF_8)

    JsonParser.parseString(json)
        .asJsonObject
        .getAsJsonArray("_flashes")
        ?.firstOrNull()
        ?.asJsonObject
        ?.entrySet()
        ?.firstOrNull()
        ?.value
        ?.asJsonArray
        ?.lastOrNull()
        ?.asString
        ?.takeIf { it.isNotBlank() }
}.getOrNull()
