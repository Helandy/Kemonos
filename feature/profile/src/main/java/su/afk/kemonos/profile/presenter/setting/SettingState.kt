package su.afk.kemonos.profile.presenter.setting

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.model.CacheTimeUi

internal class SettingState {

    data class State(
        val loading: Boolean = true,

        val appVersion: String = "",

        val kemonoUrl: String = "",
        val coomerUrl: String = "",

        val inputKemonoDomain: String = "",
        val inputCoomerDomain: String = "",

        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,

        /** cache */
        val tagsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val tagsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val creatorsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val creatorsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val favoritesPostsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val favoritesPostsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val favoritesArtistsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val favoritesArtistsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val creatorPostsCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val postContentsCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val popularKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val popularCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val favPostsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val favPostsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val favCreatorsKemonoCache: CacheTimeUi = CacheTimeUi(null, null, false),
        val favCreatorsCoomerCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val creatorProfilesCache: CacheTimeUi = CacheTimeUi(null, null, false),

        val clearInProgress: Boolean = false,
        val clearSuccess: Boolean? = null,
    )
}


internal sealed interface CacheClearAction {
    data class Tags(val site: SelectedSite) : CacheClearAction
    data class Creators(val site: SelectedSite) : CacheClearAction

    object CreatorProfiles : CacheClearAction
    object CreatorPostsPages : CacheClearAction
    object PostContents : CacheClearAction
    object PopularPosts : CacheClearAction

    object FavoritesArtists : CacheClearAction
    object FavoritesPosts : CacheClearAction
}