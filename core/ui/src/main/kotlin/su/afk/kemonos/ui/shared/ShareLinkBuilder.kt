package su.afk.kemonos.ui.shared

import su.afk.kemonos.ui.shared.model.ShareTarget

object ShareLinkBuilder {

    fun build(target: ShareTarget): String {
        val base = target.siteRoot.trimEnd('/')
        return when (target) {
            is ShareTarget.Profile ->
                "$base/${target.service}/user/${target.userId}"

            is ShareTarget.Post ->
                "$base/${target.service}/user/${target.userId}/post/${target.postId}"
        }
    }
}