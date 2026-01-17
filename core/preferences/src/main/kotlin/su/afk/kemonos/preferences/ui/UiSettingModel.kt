package su.afk.kemonos.preferences.ui

enum class CreatorViewMode { LIST, GRID }
enum class PostsViewMode { LIST, GRID }

data class UiSettingModel(

    /** debug-only: пропустить проверку API при входе */
    val skipApiCheckOnLogin: Boolean = false,

    /** Вид отображения авторов на главной */
    val creatorsViewMode: CreatorViewMode = DEFAULT_CREATORS_VIEW_MODE,

    /** Вид отображения избранное  */
    val creatorsFavoriteViewMode: CreatorViewMode = DEFAULT_CREATORS_VIEW_MODE,

    /** Посты: профиль автора */
    val profilePostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: избранное */
    val favoritePostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: популярное */
    val popularPostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: теги */
    val tagsPostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: поиск */
    val searchPostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,

    /** Предлагать рандомных авторов */
    val suggestRandomAuthors: Boolean = DEFAULT_SUGGEST_RANDOM_AUTHORS
) {
    companion object {
        val DEFAULT_CREATORS_VIEW_MODE = CreatorViewMode.LIST
        val DEFAULT_POSTS_VIEW_MODE = PostsViewMode.GRID
        val DEFAULT_SUGGEST_RANDOM_AUTHORS = false
    }
}