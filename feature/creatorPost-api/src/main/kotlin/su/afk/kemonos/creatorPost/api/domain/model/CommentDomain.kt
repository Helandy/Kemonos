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
    }
}

data class CommentRevision(
    val id: Int,
    val added: String,
    val content: String
)