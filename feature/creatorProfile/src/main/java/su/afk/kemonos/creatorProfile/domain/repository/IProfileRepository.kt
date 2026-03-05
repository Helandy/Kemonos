package su.afk.kemonos.creatorProfile.domain.repository

import su.afk.kemonos.domain.models.Profile

interface IProfileRepository {
    suspend fun getProfile(service: String, id: String): Profile?
}
