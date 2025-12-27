package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.posts.data.PostsRepository
import su.afk.kemonos.posts.domain.model.random.RandomDomain
import javax.inject.Inject

internal class GetRandomPost @Inject constructor(
    private val repository: PostsRepository,
) {
    suspend operator fun invoke(): RandomDomain = repository.getRandomPost()
}