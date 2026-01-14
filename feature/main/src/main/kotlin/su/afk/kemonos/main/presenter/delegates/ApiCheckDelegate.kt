package su.afk.kemonos.main.presenter.delegates

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.posts.api.ICheckApiUseCase
import javax.inject.Inject

internal class ApiCheckDelegate @Inject constructor(
    private val checkApiUseCase: ICheckApiUseCase,
) {
    suspend fun check(sitesToCheck: Set<SelectedSite>): ApiCheckUiResult {
        val result = checkApiUseCase(sitesToCheck)

        return if (result.allOk) ApiCheckUiResult.Success
        else ApiCheckUiResult.Failure(
            kemonoError = result.kemono.error,
            coomerError = result.coomer.error,
        )
    }

    sealed interface ApiCheckUiResult {
        data object Success : ApiCheckUiResult

        data class Failure(
            val kemonoError: ErrorItem?,
            val coomerError: ErrorItem?,
        ) : ApiCheckUiResult
    }
}