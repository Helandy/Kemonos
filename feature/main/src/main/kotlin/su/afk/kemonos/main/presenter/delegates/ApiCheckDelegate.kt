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
        else ApiCheckUiResult.Failure(errors = result.errors)
    }

    sealed interface ApiCheckUiResult {
        data object Success : ApiCheckUiResult

        data class Failure(
            val errors: Map<SelectedSite, ErrorItem>,
        ) : ApiCheckUiResult
    }
}
