package su.afk.kemonos.creatorPost.domain.media

/** Измеряет свойства удалённого медиафайла напрямую по URL. Возвращает -1, если определить не удалось. */
internal interface IRemoteMediaProbe {
    suspend fun durationMs(url: String): Long
    suspend fun sizeBytes(url: String): Long
}
