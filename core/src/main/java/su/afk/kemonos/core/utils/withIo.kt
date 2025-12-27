package su.afk.kemonos.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> withIo(crossinline block: suspend () -> T): T =
    withContext(Dispatchers.IO) { block() }