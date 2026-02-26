package su.afk.kemonos.creatorProfile.presenter.delegates

import su.afk.kemonos.creatorProfile.domain.useCase.*
import su.afk.kemonos.creatorProfile.presenter.CreatorProfileState.State
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import javax.inject.Inject

internal class LoadingTabsContent @Inject constructor(
    private val getProfileLinksUseCase: GetProfileLinksUseCase,
    private val getProfileTagsUseCase: GetProfileTagsUseCase,
    private val getProfileAnnouncementUseCase: GetProfileAnnouncementUseCase,
    private val getProfileFanCardsUseCase: GetProfileFanCardsUseCase,
    private val getProfileDmsUseCase: GetProfileDmsUseCase,
    private val getProfileSimilarUseCase: GetProfileSimilarUseCase,
) {

    /** Получение Линков */
    suspend fun checkLinks(
        setState: (State.() -> State) -> Unit,
        service: String,
        id: String,
    ) {
        val result = getProfileLinksUseCase(service, id)
        if (result.isNotEmpty()) {
            setState {
                val newTabs = showTabs.toMutableList()
                if (ProfileTab.LINKS !in newTabs) newTabs.add(ProfileTab.LINKS)

                copy(
                    showTabs = newTabs,
                    profileLinks = result
                )
            }
        }
    }

    /** Получение Тэгов */
    suspend fun checkTags(
        setState: (State.() -> State) -> Unit,
        service: String,
        id: String
    ) {
        if (service !in listOf("fanbox", "patreon", "onlyfans", "fansly", "candfans")) return
        val hasTags = getProfileTagsUseCase(service, id)
        if (hasTags.isNotEmpty()) {
            setState {
                val newTabs = showTabs.toMutableList()
                if (ProfileTab.TAGS !in newTabs) newTabs.add(ProfileTab.TAGS)

                copy(
                    showTabs = newTabs,
                    profileTags = hasTags
                )
            }
        }
    }

    /** Получение Анонсов */
    suspend fun checkAnnouncements(
        setState: (State.() -> State) -> Unit,
        service: String,
        id: String
    ) {
        if (service !in listOf("fanbox", "patreon")) return
        val hasAnnouncements = getProfileAnnouncementUseCase(service, id)
        if (hasAnnouncements.isNotEmpty()) {
            setState {
                val newTabs = showTabs.toMutableList()
                if (ProfileTab.ANNOUNCEMENTS !in newTabs) newTabs.add(ProfileTab.ANNOUNCEMENTS)

                copy(
                    showTabs = newTabs,
                    announcements = hasAnnouncements
                )
            }
        }
    }

    /** Получение Фанкарт */
    suspend fun checkFanCard(
        setState: (State.() -> State) -> Unit,
        service: String,
        id: String
    ) {
        if (service != "fanbox") return
        val result = getProfileFanCardsUseCase(service, id)
        if (result.isNotEmpty()) {
            setState {
                val newTabs = showTabs.toMutableList()
                if (ProfileTab.FANCARD !in newTabs) newTabs.add(ProfileTab.FANCARD)

                copy(
                    showTabs = newTabs,
                    fanCardsList = result
                )
            }
        }
    }

    /** Получение сообщение ЛС */
    suspend fun checkDms(
        countDm: Int?,
        setState: (State.() -> State) -> Unit,
        service: String,
        id: String
    ) {
        if (countDm == 0) return

        val result = getProfileDmsUseCase(service, id)
        if (result.isNotEmpty()) {
            setState {
                val newTabs = showTabs.toMutableList()
                if (ProfileTab.DMS !in newTabs) newTabs.add(ProfileTab.DMS)

                copy(
                    showTabs = newTabs,
                    dmList = result
                )
            }
        }
    }

    suspend fun checkSimilar(
        setState: (State.() -> State) -> Unit,
        service: String,
        id: String
    ) {
        if (service !in listOf("patreon", "onlyfans")) return

        val result = getProfileSimilarUseCase(service, id)
        if (result.isNotEmpty()) {
            setState {
                val newTabs = showTabs.toMutableList()
                if (ProfileTab.SIMILAR !in newTabs) newTabs.add(ProfileTab.SIMILAR)

                copy(
                    showTabs = newTabs,
                    similarCreators = result
                )
            }
        }
    }
}
