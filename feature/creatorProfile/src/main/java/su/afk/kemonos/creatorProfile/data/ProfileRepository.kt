package su.afk.kemonos.creatorProfile.data

import su.afk.kemonos.creatorProfile.data.api.CreatorProfileApi
import su.afk.kemonos.creatorProfile.data.dto.profile.ProfileDto.Companion.toDomain
import su.afk.kemonos.domain.models.Profile
import su.afk.kemonos.network.util.call
import su.afk.kemonos.storage.api.repository.profile.IStoreProfileRepository
import javax.inject.Inject

interface IProfileRepository {
    suspend fun getProfile(service: String, id: String): Profile?
}

internal class ProfileRepository @Inject constructor(
    private val api: CreatorProfileApi,
    private val storeProfileUseCase: IStoreProfileRepository
) : IProfileRepository {

    /** Профиль креатора */
    override suspend fun getProfile(service: String, id: String): Profile {
        storeProfileUseCase.getProfileFreshOrNull(service, id)?.let { return it }

        val fromNet = api.getProfile(service, id).call { it.toDomain() }

        storeProfileUseCase.updateProfile(fromNet)
        return fromNet
    }
}
