package su.afk.kemonos.utils.download

fun normalizeForFolder(input: String): String =
    input
        .trim()
        .replace(Regex("""[\\/:*?"<>|]"""), "_") // запрещённые символы
        .replace(Regex("""\s+"""), "_")          // пробелы → _
        .replace(Regex("""_+"""), "_")           // схлопнуть ___ → _
        .take(60)