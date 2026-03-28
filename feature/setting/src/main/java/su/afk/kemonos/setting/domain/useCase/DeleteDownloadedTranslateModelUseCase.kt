package su.afk.kemonos.setting.domain.useCase

import su.afk.kemonos.setting.domain.repository.ITranslateModelsRepository
import javax.inject.Inject

class DeleteDownloadedTranslateModelUseCase @Inject constructor(
    private val repository: ITranslateModelsRepository,
) {
    suspend operator fun invoke(modelId: String): Boolean = repository.deleteDownloadedModel(modelId)
}
