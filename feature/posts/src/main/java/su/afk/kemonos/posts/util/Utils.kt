package su.afk.kemonos.posts.util

internal object Utils {
    fun queryKey(query: String?, tag: String?): String =
        buildString {
            append(query ?: "")
            append('|')
            append(tag ?: "")
        }
}