package su.afk.kemonos.creatorProfile.navigation

import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.models.Tag
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.selectedSiteByService
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import javax.inject.Inject

class CreatorProfileNavigator @Inject constructor(
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) : ICreatorProfileNavigator {

    override suspend fun getCreatorProfileDest(
        service: String,
        id: String,
        tag: Tag?,
    ): NavKey {
        val targetSite = domainResolver.selectedSiteByService(service)
        selectedSiteUseCase.setSiteAndAwait(targetSite)

        return CreatorDestination.CreatorProfile(
            service = service,
            id = id,
            tag = tag,
        )
    }
}
