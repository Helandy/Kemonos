package su.afk.kemonos.domain

/** Откуда берётся базовый адрес для аватаров/шапок автора. */
enum class CreatorImageHost {
    /** Корень сайта: https://pawchive.pw */
    ROOT,

    /** Хост превью: https://img.kemono.cr */
    IMAGE,
}

/**
 * Правила вывода хостов медиа из базового адреса API.
 * Префикс `null` означает «использовать хост как есть».
 */
data class SiteMediaHosts(
    /** "img" -> img.example.com */
    val imageHostPrefix: String?,

    /** "file" -> file.example.com */
    val fileHostPrefix: String?,

    val creatorImageHost: CreatorImageHost,
)

/**
 * Как из [su.afk.kemonos.domain.models.AttachmentDomain] собирается ссылка на файл.
 */
enum class MediaUrlScheme {
    /** kemono/coomer/pawchive: {server}/data{path} — префикс /data добавляет клиент. */
    DATA_PREFIXED,

    /** OnlyHaven: {server}{path}, где path уже полный (/media/{sha256}/{variant}). */
    DIRECT,
}

/**
 * Что источник умеет.
 *
 * Заменяет разбросанные проверки вида `if (site == SelectedSite.P)`: компилятор
 * не ловит их при добавлении источника, а флаг заставляет описать поведение явно.
 */
data class SiteCapabilities(
    /** Вход, регистрация, серверное избранное. */
    val auth: Boolean,

    /** Раздел тегов. */
    val tags: Boolean,

    /** Лента личных сообщений. */
    val dms: Boolean,

    /** Раздел популярного. */
    val popularPosts: Boolean,

    /**
     * Популярное можно листать по периодам и датам (день/неделя/месяц).
     *
     * У OnlyHaven популярное есть, но это просто сортировка ленты по закладкам:
     * параметры периода эндпоинт молча игнорирует, поэтому панель периодов
     * для такого источника не показываем.
     */
    val popularPeriods: Boolean,

    /** Превью видео через внешний сервер метаданных. */
    val videoPreview: Boolean,

    /**
     * Источник отдаёт весь список авторов одним запросом.
     *
     * true (kemono-подобные) — список выкачивается целиком и кэшируется в Room,
     * поиск и сортировка идут локально.
     * false (OnlyHaven) — авторов больше 10к и эндпоинт постраничный,
     * поэтому листаем и ищем на сервере, без локального кэша.
     */
    val bulkCreatorList: Boolean,

    /**
     * Дополнения профиля автора семейства kemono: теги, ссылки, анонсы,
     * фан-карты, community. Источник без них не должен ходить за 404.
     */
    val profileExtras: Boolean,

    /** Комментарии к посту. */
    val comments: Boolean,
)

/**
 * Описание источника: всё, что отличает один сайт от другого, собрано в одном месте.
 *
 * Добавление нового источника = новая константа [SelectedSite] + ветка в [SiteCatalog.specOf].
 * Компилятор не даст забыть вторую часть.
 */
data class SiteSpec(
    val site: SelectedSite,

    /**
     * Стабильный слаг: ключи DataStore, имена файлов БД.
     * Менять нельзя — сломает уже сохранённые у пользователей настройки.
     */
    val slug: String,

    /** Имя для UI. */
    val displayName: String,

    val defaultApiUrl: String,

    val mediaHosts: SiteMediaHosts,

    val capabilities: SiteCapabilities,

    val mediaUrlScheme: MediaUrlScheme,

    /**
     * Самодостаточный источник: сам обслуживает все свои сервисы.
     *
     * Kemono и Coomer — федеративная пара: сервис (onlyfans/fansly/...) определяет,
     * какому из двух сайтов принадлежит контент. Остальные источники самодостаточны.
     */
    val standalone: Boolean,

    /**
     * Готов ли источник к использованию.
     *
     * false = источник объявлен, но его слой данных ещё не подключён:
     * он не показывается в UI и не участвует в стартовой проверке.
     */
    val available: Boolean = true,

    /**
     * Сервисы источника, если они известны заранее.
     * Пусто — набор вычисляется из кэша авторов.
     */
    val knownServices: Set<String> = emptySet(),

    /** Устаревшие дефолты: при совпадении молча заменяются на [defaultApiUrl]. */
    val legacyDefaultApiUrls: Set<String> = emptySet(),
)

object SiteCatalog {

    /** Сервисы, которые в паре Kemono/Coomer принадлежат Coomer. */
    val coomerServices: Set<String> = setOf("onlyfans", "fansly", "candfans")

    /** Исчерпывающий when — новая константа [SelectedSite] ломает сборку именно здесь. */
    private fun specOf(site: SelectedSite): SiteSpec = when (site) {
        SelectedSite.K -> SiteSpec(
            site = site,
            slug = "kemono",
            displayName = "Kemono",
            defaultApiUrl = "https://kemono.cr/api/",
            mediaHosts = SiteMediaHosts(
                imageHostPrefix = "img",
                fileHostPrefix = "img",
                creatorImageHost = CreatorImageHost.IMAGE,
            ),
                        mediaUrlScheme = MediaUrlScheme.DATA_PREFIXED,
            capabilities = SiteCapabilities(
                auth = true,
                tags = true,
                dms = true,
                popularPosts = true,
                popularPeriods = true,
                videoPreview = true,
                bulkCreatorList = true,
                profileExtras = true,
                comments = true,
            ),
            standalone = false,
        )

        SelectedSite.C -> SiteSpec(
            site = site,
            slug = "coomer",
            displayName = "Coomer",
            defaultApiUrl = "https://coomer.st/api/",
            mediaHosts = SiteMediaHosts(
                imageHostPrefix = "img",
                fileHostPrefix = "img",
                creatorImageHost = CreatorImageHost.IMAGE,
            ),
                        mediaUrlScheme = MediaUrlScheme.DATA_PREFIXED,
            capabilities = SiteCapabilities(
                auth = true,
                tags = true,
                dms = true,
                popularPosts = true,
                popularPeriods = true,
                videoPreview = true,
                bulkCreatorList = true,
                profileExtras = true,
                comments = true,
            ),
            standalone = false,
        )

        SelectedSite.P -> SiteSpec(
            site = site,
            slug = "pawchive",
            displayName = "Pawchive",
            defaultApiUrl = "https://pawchive.pw/api/",
            mediaHosts = SiteMediaHosts(
                imageHostPrefix = "img",
                fileHostPrefix = "file",
                creatorImageHost = CreatorImageHost.ROOT,
            ),
            mediaUrlScheme = MediaUrlScheme.DATA_PREFIXED,
            capabilities = SiteCapabilities(
                auth = false,
                tags = false,
                dms = false,
                popularPosts = true,
                popularPeriods = true,
                videoPreview = false,
                bulkCreatorList = true,
                profileExtras = true,
                comments = true,
            ),
            standalone = true,
            legacyDefaultApiUrls = setOf("https://pawchive.st/api/"),
        )

        SelectedSite.O -> SiteSpec(
            site = site,
            slug = "onlyhaven",
            displayName = "OnlyHaven",
            defaultApiUrl = "https://cum.st/api/",
            mediaHosts = SiteMediaHosts(
                /** превью: img.cum.st/thumbnail/{sha256}/preview.webp */
                imageHostPrefix = "img",
                /** файлы: e1.cum.st/media/{sha256}/{variant} */
                fileHostPrefix = "e1",
                creatorImageHost = CreatorImageHost.IMAGE,
            ),
            mediaUrlScheme = MediaUrlScheme.DIRECT,
            capabilities = SiteCapabilities(
                /** Публичный API без сессий. */
                auth = false,
                tags = false,
                /** Есть даже глобальная лента /api/v1/dms. */
                dms = true,
                popularPosts = true,
                popularPeriods = false,
                videoPreview = false,
                bulkCreatorList = false,
                profileExtras = false,
                comments = false,
            ),
            standalone = true,
            knownServices = setOf("onlyfans", "fansly"),
        )
    }

    private val specs: Map<SelectedSite, SiteSpec> =
        SelectedSite.entries.associateWith(::specOf)

    /** Все объявленные источники, в порядке объявления [SelectedSite]. */
    val all: List<SiteSpec> = SelectedSite.entries.map(specs::getValue)

    /** Источники, готовые к показу пользователю. */
    val available: List<SiteSpec> = all.filter { it.available }

    /** Порядок источников для UI. */
    val availableSites: List<SelectedSite> = available.map { it.site }

    operator fun get(site: SelectedSite): SiteSpec = specs.getValue(site)

    /** Какому источнику федеративной пары принадлежит сервис. */
    fun siteByService(service: String): SelectedSite =
        if (service in coomerServices) SelectedSite.C else SelectedSite.K
}

val SelectedSite.spec: SiteSpec get() = SiteCatalog[this]
val SelectedSite.slug: String get() = spec.slug
val SelectedSite.displayName: String get() = spec.displayName
val SelectedSite.defaultApiUrl: String get() = spec.defaultApiUrl
val SelectedSite.capabilities: SiteCapabilities get() = spec.capabilities
val SelectedSite.mediaUrlScheme: MediaUrlScheme get() = spec.mediaUrlScheme
val SelectedSite.knownServices: Set<String> get() = spec.knownServices
