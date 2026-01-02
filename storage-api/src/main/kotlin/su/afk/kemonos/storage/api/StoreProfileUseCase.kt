package su.afk.kemonos.storage.api

import su.afk.kemonos.domain.models.Profile

interface StoreProfileUseCase {

    suspend fun getProfileFreshOrNull(service: String, id: String): Profile?

    /** Обновить профиль в кэше */
    suspend fun updateProfile(profile: Profile)

    /** Очистить кэш */
    suspend fun clear()
}