package su.afk.kemonos.creatorPost.domain.media.model

/** Для локального получения информации о файле */
internal sealed interface MediaInfoState {
    data object Loading : MediaInfoState
    data class Success(val data: CommonMediaInfo) : MediaInfoState
    data class Error(val throwable: Throwable) : MediaInfoState
}