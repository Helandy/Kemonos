package su.afk.kemonos.creatorProfile.util

internal object Utils {
    fun norm(s: String?): String =
        s?.trim().orEmpty()

    fun queryKey(
        service: String,
        id: String,
        search: String?,
        tag: String?,
    ): String = buildString {
        append(service)
        append('|')
        append(id)
        append('|')
        append(norm(search))
        append('|')
        append(norm(tag))
    }
}