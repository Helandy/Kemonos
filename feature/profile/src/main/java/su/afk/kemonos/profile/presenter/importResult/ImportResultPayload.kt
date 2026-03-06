package su.afk.kemonos.profile.presenter.importResult

internal enum class ImportResultStatus {
    SUCCESS,
    FAILED,
    SKIPPED,
}

internal data class ImportResultItem(
    val rowNumber: Int,
    val target: String,
    val status: ImportResultStatus,
    val reason: String,
)

internal data class ImportResultPayload(
    val title: String,
    val summary: String,
    val items: List<ImportResultItem>,
)
