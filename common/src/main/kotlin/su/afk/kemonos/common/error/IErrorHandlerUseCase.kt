package su.afk.kemonos.common.error

import kotlinx.coroutines.CancellationException
import okhttp3.ResponseBody
import retrofit2.HttpException
import su.afk.kemonos.common.R
import su.afk.kemonos.commonscreen.navigator.IErrorNavigator
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.navigation.NavigationManager
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

interface IErrorHandlerUseCase {
    fun parse(t: Throwable, navigate: Boolean = false, retryKey: String? = null): ErrorItem
}

class ErrorHandlerUseCaseImpl @Inject constructor(
    private val strings: StringProvider,
    private val backendMessageParser: BackendMessageParser,
    private val errorNavigator: IErrorNavigator,
    private val navigationManager: NavigationManager,
) : IErrorHandlerUseCase {

    override fun parse(t: Throwable, navigate: Boolean, retryKey: String?): ErrorItem {
        if (t is CancellationException) throw t

        val base = when (t) {
            is HttpException -> parseHttp(t)

            // Coil network errors are not retrofit2.HttpException.
            // We parse status code from message ("HTTP 500") and map it consistently.
            else -> {
                val coilHttpCode = t.extractCoilHttpCode()
                when {
                    coilHttpCode != null -> ErrorItem(
                        title = httpTitle(coilHttpCode),
                        message = httpMessage(coilHttpCode),
                        code = coilHttpCode,
                        cause = t.toString()
                    )

                    t is SocketTimeoutException -> ErrorItem(
                        title = strings.get(R.string.err_title_timeout),
                        message = strings.get(R.string.err_msg_timeout),
                        cause = t.toString()
                    )

                    t is IOException -> ErrorItem(
                        title = strings.get(R.string.err_title_no_connection),
                        message = strings.get(R.string.err_msg_no_connection),
                        cause = t.toString()
                    )

                    else -> ErrorItem(
                        title = strings.get(R.string.err_title_generic),
                        message = t.message?.takeIf { it.isNotBlank() } ?: strings.get(R.string.err_msg_generic),
                        cause = t.toString()
                    )
                }
            }
        }

        val item = base.copy(retryKey = retryKey)

        if (navigate) navigationManager.navigate(errorNavigator(item))

        return item
    }

    private fun parseHttp(e: HttpException): ErrorItem {
        val code = e.code()
        val resp = e.response()

        val rawReq = resp?.raw()?.request
        val url = rawReq?.url?.toString()
        val method = rawReq?.method

        val requestId =
            resp?.headers()?.get("x-request-id")
                ?: resp?.headers()?.get("x-correlation-id")
                ?: resp?.headers()?.get("cf-ray")

        val body = resp?.errorBody()?.safeString()

        val backendMessage = backendMessageParser.extract(body)

        val title = httpTitle(code)
        val fallback = httpMessage(code)

        return ErrorItem(
            title = title,
            message = backendMessage ?: fallback,
            code = code,
            fallback = fallback,
            url = url,
            method = method,
            requestId = requestId,
            body = body,
            cause = e.message(),
        )
    }

    private fun httpTitle(code: Int): String = when (code) {
        401 -> strings.get(R.string.err_title_unauthorized)
        403 -> strings.get(R.string.err_title_forbidden)
        404 -> strings.get(R.string.err_title_not_found)
        429 -> strings.get(R.string.err_title_too_many_requests)
        500 -> strings.get(R.string.err_title_server_error)
        502 -> strings.get(R.string.err_title_bad_gateway_502)
        503 -> strings.get(R.string.err_title_service_unavailable_503)
        504 -> strings.get(R.string.err_title_gateway_timeout_504)
        else -> strings.get(R.string.err_title_http_generic, code)
    }

    private fun httpMessage(code: Int): String = when (code) {
        401 -> strings.get(R.string.err_msg_unauthorized)
        403 -> strings.get(R.string.err_msg_forbidden)
        404 -> strings.get(R.string.err_msg_not_found)
        429 -> strings.get(R.string.err_msg_too_many_requests)
        500 -> strings.get(R.string.err_msg_server_error)
        502 -> strings.get(R.string.err_msg_bad_gateway_502)
        503 -> strings.get(R.string.err_msg_service_unavailable_503)
        504 -> strings.get(R.string.err_msg_gateway_timeout_504)
        else -> strings.get(R.string.err_msg_http_generic)
    }
}

private fun Throwable.extractCoilHttpCode(): Int? {
    val isCoilHttp = this::class.qualifiedName == "coil3.network.HttpException"
    if (!isCoilHttp) return null

    val msg = message.orEmpty()
    return """\b(\d{3})\b""".toRegex()
        .find(msg)
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
}

/** errorBody можно прочитать только 1 раз — делаем безопасно */
private fun ResponseBody.safeString(maxChars: Int = 20_000): String? =
    runCatching {
        val s = string()
        if (s.length > maxChars) s.take(maxChars) + "\n…(truncated)" else s
    }.getOrNull()

/**
 * Достаём сообщение из тела:
 * - JSON поля: message/error/detail/description/title
 * - если не JSON — вернём plain text (trimmed)
 */
fun String.extractBackendMessage(): String? {
    val trimmed = trim()
    if (trimmed.isEmpty()) return null

    /** не показываем HTML-страницы ошибок (502/503) */
    val lower = trimmed.lowercase()
    if (lower.startsWith("<!doctype") || lower.startsWith("<html")) return null

    val keys = listOf("message", "error", "detail", "description", "title")
    for (k in keys) {
        val r = """"$k"\s*:\s*"([^"]+)"""".toRegex()
        val m = r.find(trimmed)?.groupValues?.getOrNull(1)
        if (!m.isNullOrBlank()) return m
    }

    /** иногда массив: {"errors":["..."]} */
    val rErrors = """"errors"\s*:\s*\[\s*"([^"]+)"""".toRegex()
    val firstErr = rErrors.find(trimmed)?.groupValues?.getOrNull(1)
    if (!firstErr.isNullOrBlank()) return firstErr

    return trimmed
}

fun ErrorItem.toFavoriteToastBar(): String {
    return listOfNotNull(
        title.takeIf { it.isNotBlank() },
        fallback?.takeIf { it.isNotBlank() }
    ).distinct()
        .joinToString(separator = "\n")
}
