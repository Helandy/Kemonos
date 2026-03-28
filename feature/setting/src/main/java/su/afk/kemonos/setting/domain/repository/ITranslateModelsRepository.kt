package su.afk.kemonos.setting.domain.repository

import su.afk.kemonos.setting.domain.model.TranslateModelInfo

interface ITranslateModelsRepository {
    suspend fun getDownloadedModels(): List<TranslateModelInfo>
    suspend fun deleteDownloadedModel(modelId: String): Boolean
}
