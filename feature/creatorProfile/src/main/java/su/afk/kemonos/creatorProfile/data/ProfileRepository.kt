package su.afk.kemonos.creatorProfile.data

import su.afk.kemonos.creatorProfile.data.api.CreatorProfileApi
import su.afk.kemonos.creatorProfile.data.dto.profile.ProfileDto.Companion.toDomain
import su.afk.kemonos.creatorProfile.domain.repository.IProfileRepository
import su.afk.kemonos.creatorProfile.data.dto.onlyhaven.OnlyHavenProfileDto.Companion.toDomain as toOnlyHavenDomain
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.network.util.call
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.storage.api.repository.profile.IStoreProfileRepository
import javax.inject.Inject

internal class ProfileRepository @Inject constructor(
    private val api: CreatorProfileApi,
    private val storeProfileUseCase: IStoreProfileRepository,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) : IProfileRepository {

    /** Профиль креатора */
    override suspend fun getProfile(service: String, id: String): Profile {
        val site = selectedSiteUseCase.getSite()
        storeProfileUseCase.getProfileFreshOrNull(site, service, id)?.let { return it }

        val fromNet = if (site == SelectedSite.O) {
            api.getOnlyHavenProfile(service, id).call { it.toOnlyHavenDomain() }
        } else {
            api.getProfile(service, id).call { it.toDomain() }
        }

        storeProfileUseCase.updateProfile(site, fromNet)
        return fromNet
    }
}
