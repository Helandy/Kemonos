package su.afk.kemonos.core.api.domain.useCase

import kotlinx.coroutines.flow.StateFlow

interface IGetBaseUrlsUseCase {
    val kemonoUrl: StateFlow<String>
    val coomerUrl: StateFlow<String>
}