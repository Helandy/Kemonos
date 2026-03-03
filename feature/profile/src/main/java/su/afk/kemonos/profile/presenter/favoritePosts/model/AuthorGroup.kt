package su.afk.kemonos.profile.presenter.favoritePosts.model

import su.afk.kemonos.domain.models.PostDomain

interface data
class AuthorGroup(
    val service: String,
    val userId: String,
    val authorName: String,
    val posts: MutableList<PostDomain>,
)