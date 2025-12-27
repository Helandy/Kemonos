package su.afk.kemonos.storage.useCases

import su.afk.kemonos.domain.domain.models.Profile
import su.afk.kemonos.storage.api.StoreProfileUseCase
import su.afk.kemonos.storage.repository.profile.IStoreProfileRepository
import javax.inject.Inject

internal class StoreProfileUseCaseImpl @Inject constructor(
    private val storeProfileRepository: IStoreProfileRepository
) : StoreProfileUseCase {

    /** Получить профиль из кэша */
    override suspend fun getProfileFreshOrNull(service: String, id: String): Profile? =
        storeProfileRepository.getProfileFreshOrNull(service, id)

    /** Обновить профиль в кэше */
    override suspend fun updateProfile(profile: Profile) = storeProfileRepository.updateProfile(profile)

    /** Очистить кэш */
    override suspend fun clear() = storeProfileRepository.clear()
}