package su.afk.kemonos.creatorPost.api.domain.model

data class CommentDomain(
    val id: String,
    val commenter: String,
    val commenter_name: String?,
    val content: String,
    val published: String,
    val revisions: List<CommentRevision>
) {
    companion object {
        fun emptyComment(): CommentDomain =
            CommentDomain(
                id = "",
                commenter = "",
                commenter_name = null,
                content = "",
                published = "",
                revisions = emptyList()
            )

        fun List<CommentDomain>.toCachePayload(): List<CommentDomain> {
            return if (isEmpty()) listOf(emptyComment()) else this
        }

        fun List<CommentDomain>.toExternalComments(): List<CommentDomain> {
            return filterNot { it.isCacheEmptyMarker() }
        }

        fun CommentDomain.isCacheEmptyMarker(): Boolean {
            return id.isBlank() &&
                    commenter.isBlank() &&
                    commenter_name.isNullOrBlank() &&
                    content.isBlank() &&
                    published.isBlank() &&
                    revisions.isEmpty()
        }
    }
}

data class CommentRevision(
    val id: Int,
    val added: String,
    val content: String
)