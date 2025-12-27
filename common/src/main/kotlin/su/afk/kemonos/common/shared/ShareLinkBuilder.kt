package su.afk.kemonos.common.shared

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