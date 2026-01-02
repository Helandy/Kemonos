package su.afk.kemonos.main.presenter.delegates

import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.posts.api.ICheckApiUseCase
import javax.inject.Inject

internal class ApiCheckDelegate @Inject constructor(
    private val checkApiUseCase: ICheckApiUseCase,
) {
    suspend fun check(): ApiCheckUiResult {
        val check = checkApiUseCase()
        return if (check.success) ApiCheckUiResult.Success
        else ApiCheckUiResult.Failure(check.error)
    }

    sealed interface ApiCheckUiResult {
        data object Success : ApiCheckUiResult
        data class Failure(val error: ErrorItem?) : ApiCheckUiResult
    }
}