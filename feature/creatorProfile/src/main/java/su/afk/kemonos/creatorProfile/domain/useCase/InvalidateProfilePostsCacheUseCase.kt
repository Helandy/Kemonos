package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.util.Utils.queryKey
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import javax.inject.Inject

/** Сбрасывает кэш страниц постов профиля для выбранного запроса (service/id/search/tag). */
internal class InvalidateProfilePostsCacheUseCase @Inject constructor(
    private val postsCache: IStorageCreatorPostsRepository,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) {
    suspend operator fun invoke(service: String, id: String, search: String?, tag: String?) {
        val key = queryKey(service = service, id = id, search = search, tag = tag)
        postsCache.clearQuery(selectedSiteUseCase.getSite(), key)
    }
}
