package su.afk.kemonos.storage.api.repository.discord

import su.afk.kemonos.storage.api.repository.community.CommunityCacheType

interface IStoreDiscordRepository {
    suspend fun getFreshJsonOrNull(id: String, type: CommunityCacheType): String?
    suspend fun putJson(id: String, type: CommunityCacheType, json: String)
    suspend fun clearCacheOver7Days()
    suspend fun clearAll()
}
