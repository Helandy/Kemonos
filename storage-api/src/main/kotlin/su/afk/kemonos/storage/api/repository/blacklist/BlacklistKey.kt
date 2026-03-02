package su.afk.kemonos.storage.api.repository.blacklist

fun blacklistKey(service: String, creatorId: String): String =
    "${service.lowercase()}:$creatorId"
