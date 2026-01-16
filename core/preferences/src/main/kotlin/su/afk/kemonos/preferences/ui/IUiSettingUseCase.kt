package su.afk.kemonos.preferences.ui

import kotlinx.coroutines.flow.Flow

interface IUiSettingUseCase {
    val prefs: Flow<UiSettingModel>

    /** Debug: пропустить проверку API при входе */
    suspend fun setSkipApiCheckOnLogin(value: Boolean)

    /** Вид отображения авторов  */
    suspend fun setCreatorsViewMode(value: CreatorViewMode)

    /** Вид отображения авторов избранное */
    suspend fun setCreatorsFavoriteViewMode(value: CreatorViewMode)

    /** Вид отображения постов */
    suspend fun setProfilePostsViewMode(value: PostsViewMode)
    suspend fun setFavoritePostsViewMode(value: PostsViewMode)
    suspend fun setPopularPostsViewMode(value: PostsViewMode)
    suspend fun setTagsPostsViewMode(value: PostsViewMode)
    suspend fun setSearchPostsViewMode(value: PostsViewMode)
}