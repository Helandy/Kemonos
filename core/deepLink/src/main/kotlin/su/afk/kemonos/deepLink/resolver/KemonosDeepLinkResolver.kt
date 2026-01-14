package su.afk.kemonos.deepLink.resolver

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.deepLink.data.Domains
import javax.inject.Inject

// todo продумать функциональность на каждый модуль
// todo добавить экран неудачи открытии (если линка кривая пришла)
internal class KemonosDeepLinkResolver @Inject constructor(
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val creatorPostNavigator: ICreatorPostNavigator,
) : DeepLinkResolver {

    override suspend fun resolve(uri: Uri): NavKey? {
        val hostOk = uri.host == Domains.KEMONO || uri.host == Domains.COOMER
        if (!hostOk) return null

        val s = uri.pathSegments
        if (s.isEmpty()) return null

        // Форматы:
        // 1) /{service}/user/{id}
        // 2) /{service}/user/{id}/post/{postId}

        val service = s.getOrNull(0) ?: return null
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
                isFresh = false,
            )
        }
    }
}
