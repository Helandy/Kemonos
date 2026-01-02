package su.afk.kemonos.creatorProfile.api

import su.afk.kemonos.domain.models.Profile

interface IGetProfileUseCase {
    suspend operator fun invoke(service: String, id: String): Profile?
}