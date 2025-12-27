package su.afk.kemonos.creatorProfile.domain.useCase

import su.afk.kemonos.creatorProfile.api.domain.models.profileAnnouncements.ProfileAnnouncement
import su.afk.kemonos.creatorProfile.data.CreatorsRepository
import javax.inject.Inject


internal class GetProfileAnnouncementUseCase @Inject constructor(
    private val repository: CreatorsRepository
) {
    suspend operator fun invoke(service: String, id: String): List<ProfileAnnouncement> {
        return repository.getProfileAnnouncements(service, id)
    }
}