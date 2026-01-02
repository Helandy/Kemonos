package su.afk.kemonos.domain.models

data class ErrorItem(
    val title: String,
    val message: String,

    val code: Int? = null,
    val url: String? = null,
    val method: String? = null,

    val requestId: String? = null,
    val body: String? = null,
    val cause: String? = null,

    val retryKey: String? = null,
)