package su.afk.kemonos.setting.domain.useCase

import su.afk.kemonos.setting.domain.model.TranslateModelInfo
import su.afk.kemonos.setting.domain.repository.ITranslateModelsRepository
import javax.inject.Inject

class GetDownloadedTranslateModelsUseCase @Inject constructor(
    private val repository: ITranslateModelsRepository,
) {
    suspend operator fun invoke(): List<TranslateModelInfo> = repository.getDownloadedModels()
}
