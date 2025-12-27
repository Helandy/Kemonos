package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.api.domain.tags.Tags
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.data.PostsRepository
import javax.inject.Inject

internal class GetAllTagsUseCase @Inject constructor(
    private val repository: PostsRepository,
) {
    suspend operator fun invoke(site: SelectedSite): List<Tags> {
        return repository.getTags(site)
    }
}