package su.afk.kemonos.deepLink.resolver

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.deepLink.data.Domains
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import javax.inject.Inject

// todo продумать функциональность на каждый модуль
// todo добавить экран неудачи открытии (если линка кривая пришла)
internal class KemonosDeepLinkResolver @Inject constructor(
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val creatorPostNavigator: ICreatorPostNavigator,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
) : DeepLinkResolver {

    override suspend fun resolve(uri: Uri): NavKey? {
        val hostOk = uri.host == Domains.KEMONO ||
                uri.host == Domains.COOMER ||
                uri.host == Domains.PAWCHIVE ||
                uri.host == Domains.PAWCHIVE_LEGACY ||
                uri.host == Domains.ONLYHAVEN
        if (!hostOk) return null

        val site = siteByHost(uri.host)
        /**
         * Именно await: навигаторы ниже перечитывают выбранный сайт, чтобы
         * определить владельца сервиса. С асинхронной установкой они увидят
         * прежний источник и уведут ссылку не туда.
         */
        selectedSiteUseCase.setSiteAndAwait(site)

        val s = uri.pathSegments
        if (s.isEmpty()) return null

        /** У OnlyHaven ссылки другой формы: /creators/{service}/{id}[/post/{postId}] */
        if (site == SelectedSite.O) return resolveOnlyHaven(s)

        // Discord:
        // 1) /discord/server/{serverId}
        // 2) /discord/server/{serverId}/{channelId}
        if (s.getOrNull(0) == "discord" && s.getOrNull(1) == "server") {
            val serverId = s.getOrNull(2)
            val channelId = s.getOrNull(3)
            if (serverId.isNullOrBlank()) return null

            if (!channelId.isNullOrBlank()) {
                return creatorProfileNavigator.getCommunityChatDest(
                    service = "discord",
                    creatorId = serverId,
                    channelId = channelId,
                    channelName = channelId,
                )
            }

            return creatorProfileNavigator.getCreatorProfileDest(
                service = "discord",
                id = serverId,
            )
        }

        // Форматы:
        // 1) /{service}/user/{id}
        // 2) /{service}/user/{id}/post/{postId}

        val service = s.getOrNull(0) ?: return null
        if (service == "discord") return null
        val isUser = s.getOrNull(1) == "user"
        val id = s.getOrNull(2)

        if (!isUser || id.isNullOrBlank()) return null

        // /{service}/user/{id}/post/{postId}
        val isPost = s.getOrNull(3) == "post"
        val postId = s.getOrNull(4)

        return if (isPost && !postId.isNullOrBlank()) {
            creatorPostNavigator.getCreatorPostDest(
                id = id,
                service = service,
                postId = postId,
                showBarCreator = true,
            )
        } else {
            creatorProfileNavigator.getCreatorProfileDest(
                service = service,
                id = id,
            )
        }
    }

    private suspend fun resolveOnlyHaven(segments: List<String>): NavKey? {
        if (segments.getOrNull(0) != "creators") return null

        val service = segments.getOrNull(1)?.takeIf { it.isNotBlank() } ?: return null
        val id = segments.getOrNull(2)?.takeIf { it.isNotBlank() } ?: return null

        val postId = segments.getOrNull(4)?.takeIf { segments.getOrNull(3) == "post" && it.isNotBlank() }

        return if (postId != null) {
            creatorPostNavigator.getCreatorPostDest(
                id = id,
                service = service,
                postId = postId,
                showBarCreator = true,
            )
        } else {
            creatorProfileNavigator.getCreatorProfileDest(service = service, id = id)
        }
    }

    private fun siteByHost(host: String?): SelectedSite =
        when (host) {
            Domains.COOMER -> SelectedSite.C
            Domains.PAWCHIVE, Domains.PAWCHIVE_LEGACY -> SelectedSite.P
            Domains.ONLYHAVEN -> SelectedSite.O
            else -> SelectedSite.K
        }
}
