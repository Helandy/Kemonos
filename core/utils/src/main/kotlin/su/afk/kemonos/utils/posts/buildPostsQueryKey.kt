package su.afk.kemonos.utils.posts

fun buildPostsQueryKey(query: String?, tag: String?): String =
    buildString {
        append(query ?: "")
        append('|')
        append(tag ?: "")
    }
