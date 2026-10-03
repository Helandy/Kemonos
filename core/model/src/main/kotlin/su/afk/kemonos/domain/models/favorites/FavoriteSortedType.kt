package su.afk.kemonos.domain.models.favorites

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
