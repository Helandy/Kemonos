package su.afk.kemonos.common.view.posts

import su.afk.kemonos.domain.models.PostDomain

fun PostDomain.stableKey(): String = "$service:$userId:$id"