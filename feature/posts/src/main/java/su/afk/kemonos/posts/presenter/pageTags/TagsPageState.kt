package su.afk.kemonos.posts.presenter.pageTags

import su.afk.kemonos.api.domain.tags.Tags

internal data class TagsPageState(
    val loading: Boolean = false,

    /** Все тэги */
    val allTags: List<Tags> = emptyList(),
    val tags: List<Tags> = emptyList(),
    val selectTag: String? = null,

    val searchQuery: String = "",
)