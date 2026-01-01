package su.afk.kemonos.main.presenter.delegates

import su.afk.kemonos.common.repository.checkApi.ICheckApiRepository
import su.afk.kemonos.domain.domain.models.ErrorItem
import javax.inject.Inject

internal class ApiCheckDelegate @Inject constructor(
    private val repository: ICheckApiRepository,
) {
    suspend fun check(): ApiCheckUiResult {
        val check = repository.getApiCheck()
        return if (check.success) ApiCheckUiResult.Success
        else ApiCheckUiResult.Failure(check.error)
    }

    sealed interface ApiCheckUiResult {
        data object Success : ApiCheckUiResult
        data class Failure(val error: ErrorItem?) : ApiCheckUiResult
    }
}