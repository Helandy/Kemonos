package su.afk.kemonos.profile.presenter.importResult

import su.afk.kemonos.ui.presenter.baseViewModel.UiText

internal enum class ImportResultStatus {
    SUCCESS,
    FAILED,
    SKIPPED,
}

internal data class ImportResultItem(
    val rowNumber: Int,
    val target: UiText,
    val status: ImportResultStatus,
    val reason: UiText,
)

internal data class ImportResultPayload(
    val title: UiText,
    val summary: UiText,
    val items: List<ImportResultItem>,
)
