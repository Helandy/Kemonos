package su.afk.kemonos.common.util.selectDomain

private val coomerServicesList = listOf(
    "onlyfans",
    "fansly",
    "candfans",
)

// todo Вынести в наcтройки
/** Возвращает базовый IMG-домен для сервиса */
fun getImageBaseUrlByService(service: String): String {
    return if (service in coomerServicesList) "https://img.coomer.st"
    else "https://img.kemono.cr"
}

/** Возвращает базовый домен для сервиса */
fun getBaseUrlByService(service: String): String {
    return if (service in coomerServicesList) "https://coomer.st"
    else "https://kemono.cr"
}