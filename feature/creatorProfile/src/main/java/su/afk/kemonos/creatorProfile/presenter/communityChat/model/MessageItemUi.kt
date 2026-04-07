package su.afk.kemonos.creatorProfile.presenter.communityChat.model

import su.afk.kemonos.preferences.ui.DateFormatMode

internal data class MessageItemUi(
    val fallbackBaseUrl: String,
    val dateMode: DateFormatMode,
    val showAuthorAvatar: Boolean,
    val autoplayVideoInline: Boolean,
    val blurImages: Boolean,
    val translation: MessageTranslationUi,
)

internal data class MessageTranslationUi(
    val expandedIds: Set<String> = emptySet(),
    val loadingIds: Set<String> = emptySet(),
    val translatedTextById: Map<String, String> = emptyMap(),
    val errorById: Map<String, String> = emptyMap(),
) {
    fun isExpanded(messageId: String): Boolean = messageId in expandedIds

    fun isLoading(messageId: String): Boolean = messageId in loadingIds

    fun translatedText(messageId: String): String? = translatedTextById[messageId]

    fun error(messageId: String): String? = errorById[messageId]
}

internal data class MessageItemActions(
    val onOpenMedia: (CommunityMedia) -> Unit,
    val onOpenUrl: (String) -> Unit,
    val onToggleTranslate: (String, String) -> Unit,
)
