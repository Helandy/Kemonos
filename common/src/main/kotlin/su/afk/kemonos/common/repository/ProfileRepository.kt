package su.afk.kemonos.common.repository

import su.afk.kemonos.common.api.KemonoApi
import su.afk.kemonos.common.data.profile.ProfileDto.Companion.toDomain
import su.afk.kemonos.core.api.domain.net.helpers.call
import su.afk.kemonos.domain.domain.models.Profile
import su.afk.kemonos.storage.api.StoreProfileUseCase
import javax.inject.Inject

interface IProfileRepository {
    suspend fun getProfile(service: String, id: String): Profile?
}

internal class ProfileRepository @Inject constructor(
    private val api: KemonoApi,
    private val storeProfileUseCase: StoreProfileUseCase
) : IProfileRepository {

    /** Профиль креатора */
    override suspend fun getProfile(service: String, id: String): Profile {
        storeProfileUseCase.getProfileFreshOrNull(service, id)?.let { return it }

        val fromNet = api.getProfile(service, id).call { it.toDomain() }

        storeProfileUseCase.updateProfile(fromNet)
        return fromNet
    }
}
