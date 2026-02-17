package su.afk.kemonos.ui.shared.model

sealed interface ShareTarget {
    val siteRoot: String
    val service: String
    val userId: String

    data class Profile(
        override val siteRoot: String,
        override val service: String,
        override val userId: String,
    ) : ShareTarget

    data class Post(
        override val siteRoot: String,
        override val service: String,
        override val userId: String,
        val postId: String,
    ) : ShareTarget
}