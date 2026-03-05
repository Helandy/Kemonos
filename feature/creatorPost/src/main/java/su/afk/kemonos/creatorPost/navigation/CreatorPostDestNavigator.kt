package su.afk.kemonos.creatorPost.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.selectedSiteByService
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import javax.inject.Inject

class CreatorPostDestNavigator @Inject constructor(
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) : ICreatorPostNavigator {

    override suspend fun getCreatorPostDest(
        id: String,
        service: String,
        postId: String,
        showBarCreator: Boolean
    ): NavKey {
        val targetSite = domainResolver.selectedSiteByService(service)
        selectedSiteUseCase.setSiteAndAwait(targetSite)

        return CreatorPostDestination.CreatorPost(
            id = id,
            service = service,
            postId = postId,
            showBarCreator = showBarCreator
        )
    }
}
