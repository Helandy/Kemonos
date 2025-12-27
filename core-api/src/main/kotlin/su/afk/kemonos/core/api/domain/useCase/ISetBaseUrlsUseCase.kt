package su.afk.kemonos.core.api.domain.useCase

interface ISetBaseUrlsUseCase {
    suspend operator fun invoke(kemonoUrl: String, coomerUrl: String)
}