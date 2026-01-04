package su.afk.kemonos.preferences.model

data class CacheTimeUi(
    val lastMs: Long?,
    val nextMs: Long?,
    val isFresh: Boolean,
) {
    fun CacheTimeUi.lastUi(orNever: String, formatter: (Long) -> String): String =
        lastMs?.let(formatter) ?: orNever

    fun CacheTimeUi.nextUi(orNever: String, formatter: (Long) -> String): String =
        nextMs?.let(formatter) ?: orNever
}