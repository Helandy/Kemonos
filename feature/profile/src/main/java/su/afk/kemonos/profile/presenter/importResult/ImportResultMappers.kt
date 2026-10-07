package su.afk.kemonos.profile.presenter.importResult

import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.domain.blacklist.BlacklistImportEntry
import su.afk.kemonos.profile.domain.blacklist.BlacklistImportEntryReason
import su.afk.kemonos.profile.domain.blacklist.BlacklistImportEntryStatus
import su.afk.kemonos.profile.domain.favorites.FavoritesImportEntry
import su.afk.kemonos.profile.domain.favorites.FavoritesImportEntryReason
import su.afk.kemonos.profile.domain.favorites.FavoritesImportEntryStatus
import su.afk.kemonos.ui.presenter.baseViewModel.UiText

internal fun FavoritesImportEntry.toImportResultItem(): ImportResultItem = ImportResultItem(
    rowNumber = rowNumber,
    target = target.toTargetText(),
    status = when (status) {
        FavoritesImportEntryStatus.SUCCESS -> ImportResultStatus.SUCCESS
        FavoritesImportEntryStatus.FAILED -> ImportResultStatus.FAILED
        FavoritesImportEntryStatus.SKIPPED -> ImportResultStatus.SKIPPED
    },
    reason = when (reason) {
        FavoritesImportEntryReason.NONE -> reasonNone
        FavoritesImportEntryReason.INVALID_ITEM -> reasonInvalidItem
        FavoritesImportEntryReason.DUPLICATE_IN_FILE -> reasonDuplicate
        FavoritesImportEntryReason.REQUEST_FAILED -> reasonRequestFailed
    },
)

internal fun BlacklistImportEntry.toImportResultItem(): ImportResultItem = ImportResultItem(
    rowNumber = rowNumber,
    target = target.toTargetText(),
    status = when (status) {
        BlacklistImportEntryStatus.SUCCESS -> ImportResultStatus.SUCCESS
        BlacklistImportEntryStatus.FAILED -> ImportResultStatus.FAILED
        BlacklistImportEntryStatus.SKIPPED -> ImportResultStatus.SKIPPED
    },
    reason = when (reason) {
        BlacklistImportEntryReason.NONE -> reasonNone
        BlacklistImportEntryReason.INVALID_ITEM -> reasonInvalidItem
        BlacklistImportEntryReason.DUPLICATE_IN_FILE -> reasonDuplicate
        BlacklistImportEntryReason.REQUEST_FAILED -> reasonRequestFailed
    },
)

/** Payload для случая, когда импорт упал целиком: одна строка с причиной ошибки. */
internal fun importFailurePayload(
    title: UiText,
    summary: UiText,
    cause: Throwable,
): ImportResultPayload = ImportResultPayload(
    title = title,
    summary = summary,
    items = listOf(
        ImportResultItem(
            rowNumber = 1,
            target = unknownTarget,
            status = ImportResultStatus.FAILED,
            reason = cause.message?.let(UiText::DynamicString) ?: reasonRequestFailed,
        )
    ),
)

private val unknownTarget = UiText.Resource(R.string.profile_import_result_unknown_target)
private val reasonNone = UiText.Resource(R.string.profile_import_reason_none)
private val reasonInvalidItem = UiText.Resource(R.string.profile_import_reason_invalid_item)
private val reasonDuplicate = UiText.Resource(R.string.profile_import_reason_duplicate)
private val reasonRequestFailed = UiText.Resource(R.string.profile_import_reason_request_failed)

private fun String.toTargetText(): UiText =
    if (isBlank()) unknownTarget else UiText.DynamicString(this)
