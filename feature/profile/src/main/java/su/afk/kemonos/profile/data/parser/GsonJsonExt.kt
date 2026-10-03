package su.afk.kemonos.profile.data.parser

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

internal fun parseJsonArrayOrNull(rawJson: String): JsonArray? =
    runCatching { JsonParser.parseString(rawJson) }
        .getOrNull()
        ?.takeIf { it.isJsonArray }
        ?.asJsonArray

internal fun JsonObject.stringField(name: String): String? =
    runCatching { get(name) }
        .getOrNull()
        ?.takeIf { !it.isJsonNull }
        ?.let { runCatching { it.asString }.getOrNull() }
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

internal fun JsonObject.longField(name: String): Long? =
    runCatching { get(name) }
        .getOrNull()
        ?.takeIf { !it.isJsonNull }
        ?.let { runCatching { it.asLong }.getOrNull() }
