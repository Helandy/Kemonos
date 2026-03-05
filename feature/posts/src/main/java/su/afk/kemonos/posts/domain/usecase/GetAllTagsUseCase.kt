package su.afk.kemonos.posts.domain.usecase

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.posts.api.tags.Tags
import su.afk.kemonos.posts.domain.repository.IPostsRepository
import javax.inject.Inject

internal class GetAllTagsUseCase @Inject constructor(
    private val repository: IPostsRepository,
) {
    suspend operator fun invoke(site: SelectedSite, forceRefresh: Boolean = false): List<Tags> {
        return repository.getTags(site, forceRefresh = forceRefresh)
    }
}
