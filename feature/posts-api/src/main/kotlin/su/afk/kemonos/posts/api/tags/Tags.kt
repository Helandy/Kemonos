package su.afk.kemonos.posts.api.tags

data class Tags(
    val tags: String?,
    val count: Int?,
) {
    companion object {
        fun List<Tags>.normalizeTags(): List<Tags> = asSequence()
            .mapNotNull { tag ->
                val normalizedName = tag.tags?.trim()?.ifEmpty { null } ?: return@mapNotNull null
                Tags(
                    tags = normalizedName,
                    count = tag.count?.coerceAtLeast(0),
                )
            }
            .distinctBy { it.tags?.lowercase() }
            .sortedByDescending { it.count ?: 0 }
            .toList()

    }
}