package su.afk.kemonos.common.components.posts.filter

import su.afk.kemonos.common.util.isImageFile
import su.afk.kemonos.common.util.isVideoFile
import su.afk.kemonos.domain.models.PostDomain

data class PostMediaFilter(
    val hasVideo: Boolean = false,
    val hasAttachments: Boolean = false,
    val hasImages: Boolean = false,
) {
    val isActive: Boolean
        get() = hasVideo || hasAttachments || hasImages
}

fun PostDomain.matchesMediaFilter(filter: PostMediaFilter): Boolean {
    if (filter.hasAttachments && attachments.isEmpty()) return false

    if (filter.hasVideo) {
        val hasVideoFile = isVideoFile(file?.path) || attachments.any { isVideoFile(it.path) }
        if (!hasVideoFile) return false
    }

    if (filter.hasImages) {
        val hasImageFile = isImageFile(file?.path) || attachments.any { isImageFile(it.path) }
        if (!hasImageFile) return false
    }

    return true
}
