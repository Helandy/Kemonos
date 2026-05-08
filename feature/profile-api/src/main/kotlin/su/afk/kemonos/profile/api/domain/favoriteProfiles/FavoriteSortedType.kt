package su.afk.kemonos.profile.api.domain.favoriteProfiles

import kotlinx.serialization.Serializable

/**
 *  дата новой публикации
 *  дата добавления в избранное
 *  дата реимпорта
 */
@Serializable
enum class FavoriteSortedType {
    NewPostsDate,
    FavedDate,
    ReimportDate,
}
