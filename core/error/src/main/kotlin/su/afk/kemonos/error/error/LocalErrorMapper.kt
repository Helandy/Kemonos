package su.afk.kemonos.error.error

import androidx.compose.runtime.staticCompositionLocalOf
import su.afk.kemonos.domain.models.ErrorItem

fun interface ErrorMapper {
    fun map(t: Throwable): ErrorItem
}

val LocalErrorMapper = staticCompositionLocalOf<ErrorMapper> {
    error("LocalErrorMapper is not provided")
}

/** Для превью */
val PreviewErrorMapper = ErrorMapper { t ->
    ErrorItem(
        title = "Preview error",
        message = t.message ?: "Unknown error"
    )
}