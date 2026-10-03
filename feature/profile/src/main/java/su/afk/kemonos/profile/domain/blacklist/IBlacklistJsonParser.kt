package su.afk.kemonos.profile.domain.blacklist

import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor

internal interface IBlacklistJsonParser {
    /** One element per row, `null` for an invalid row. Throws if the file is not a JSON array. */
    fun parse(rawJson: String): List<BlacklistedAuthor?>
}
