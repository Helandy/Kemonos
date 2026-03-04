package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.posts.domain.model.random.RandomDomain
import su.afk.kemonos.posts.domain.repository.IPostsRepository
import javax.inject.Inject

internal class GetRandomPost @Inject constructor(
    private val repository: IPostsRepository,
) {
    suspend operator fun invoke(): RandomDomain = repository.getRandomPost()
}