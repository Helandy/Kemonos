package su.afk.kemonos.common.presenter.screens.postsScreen

import su.afk.kemonos.domain.models.PostDomain

fun PostDomain.stableKey(): String = "$service:$userId:$id"