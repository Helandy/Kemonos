package su.afk.kemonos.posts.data.dto.popular.pawchive

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import su.afk.kemonos.domain.models.FileDomain
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.api.popular.PopularInfo
import su.afk.kemonos.posts.api.popular.PopularNavigationDates
import su.afk.kemonos.posts.api.popular.PopularPosts
import su.afk.kemonos.posts.api.popular.PopularProps
import su.afk.kemonos.posts.domain.model.popular.Period
import java.time.LocalDate

/**
 * Pawchive не отдаёт популярное через JSON API (/api/v1/posts/popular -> 404),
 * популярное живёт только HTML-страницей /posts/popular.
 * Парсим её в ту же доменную модель, что и JSON у Kemono/Coomer.
 */
internal fun parsePawchivePopularHtml(
    html: String,
    period: Period,
    requestedDate: String?,
    offset: Int,
): PopularPosts {
    val document = Jsoup.parse(html)

    /**
     * Нет блока навигации по датам -> это не страница популярного
     * (редизайн, ошибка, заглушка Cloudflare). Бросаем, чтобы сработал
     * штатный обработчик ошибок и откат на протухший кэш.
     */
    val datesBlock = document.selectFirst("#paginator-dates")
        ?: error("Pawchive popular page has unexpected markup")

    val posts = document.select("article.post-card").mapNotNull { it.toPostDomain() }

    val navigation = PeriodNavigation(
        day = datesBlock.parseNavSlots("#daily", requestedDate),
        week = datesBlock.parseNavSlots("#weekly", requestedDate),
        month = datesBlock.parseNavSlots("#monthly", requestedDate),
    )

    val currentDate = requestedDate ?: navigation.currentFor(period)
    val range = periodRange(period, currentDate)

    return PopularPosts(
        props = PopularProps(
            count = document.parseTotalCount() ?: (offset + posts.size),
            earliestDateForPopular = null,
            /**
             * today используется только в isNextAllowed(next, today).
             * Максимум из всех дат страницы делает стрелку "вперёд" активной
             * ровно тогда, когда сайт сам отдал ссылку next.
             */
            today = navigation.latestDate().orEmpty(),
        ),
        info = PopularInfo(
            date = currentDate,
            maxDate = range?.second,
            minDate = range?.first,
            navigationDates = PopularNavigationDates(
                recent = emptyList(),
                day = navigation.day.toApiOrder(),
                week = navigation.week.toApiOrder(),
                month = navigation.month.toApiOrder(),
            ),
            rangeDesc = null,
            scale = period.name.lowercase(),
        ),
        posts = posts,
    )
}

/** Слоты одной строки периода на странице: «prev», центр, «next» */
private data class NavSlots(
    val prev: String?,
    val current: String?,
    val next: String?,
) {
    /** DatesNavigate.tripleFor ждёт порядок [prev, next, current]; пустая строка = слот недоступен */
    fun toApiOrder(): List<String> = listOf(prev.orEmpty(), next.orEmpty(), current.orEmpty())

    fun dates(): List<String> = listOfNotNull(prev, current, next)
}

private data class PeriodNavigation(
    val day: NavSlots,
    val week: NavSlots,
    val month: NavSlots,
) {
    fun currentFor(period: Period): String? = when (period) {
        Period.DAY -> day.current
        Period.WEEK -> week.current
        Period.MONTH -> month.current
        Period.RECENT -> null
    }

    fun latestDate(): String? = (day.dates() + week.dates() + month.dates()).maxOrNull()
}

private val DATE_IN_HREF = Regex("""date=(\d{4}-\d{2}-\d{2})""")
private val TOTAL_COUNT = Regex("""of\s+([\d\s,]+)""")
private val FAV_COUNT = Regex("""([\d,]+)\s+favorites?""", RegexOption.IGNORE_CASE)
private const val THUMBNAIL_PREFIX = "/thumbnail/data"

/**
 * Строка периода выглядит так:
 * <div id="daily"><span><a ...>« prev</a></span><span><a ...>Day</a></span><span><a ...>next »</a></span></div>
 * У активного периода центр — <strong> без ссылки, недоступная стрелка тоже без ссылки.
 */
private fun Element.parseNavSlots(blockSelector: String, requestedDate: String?): NavSlots {
    val block = selectFirst(blockSelector) ?: return NavSlots(null, null, null)
    val slots = block.children().filter { it.tagName() == "span" }

    return NavSlots(
        prev = slots.getOrNull(0).dateFromLink(),
        /** активный период не ссылается сам на себя — тогда текущая дата это запрошенная */
        current = slots.getOrNull(1).dateFromLink() ?: requestedDate,
        next = slots.getOrNull(2).dateFromLink(),
    )
}

private fun Element?.dateFromLink(): String? {
    val href = this?.selectFirst("a[href]")?.attr("href") ?: return null
    return DATE_IN_HREF.find(href)?.groupValues?.getOrNull(1)
}

/** "Showing 451 - 500 of 500" -> 500, запасной вариант — <meta name="count"> */
private fun org.jsoup.nodes.Document.parseTotalCount(): Int? {
    val fromPaginator = (select("#paginator-top small") + select("small"))
        .firstNotNullOfOrNull { small -> TOTAL_COUNT.find(small.text())?.groupValues?.getOrNull(1) }
        ?.toIntOrNullDigits()

    return fromPaginator ?: selectFirst("meta[name=count]")?.attr("content")?.toIntOrNullDigits()
}

private fun Element.toPostDomain(): PostDomain? {
    val href = selectFirst("a[href]")?.attr("href").orEmpty()
    val fromHref = HREF_POST.find(href)?.groupValues

    val id = attr("data-id").ifBlank { fromHref?.getOrNull(3).orEmpty() }
    val userId = attr("data-user").ifBlank { fromHref?.getOrNull(2).orEmpty() }
    val service = attr("data-service").ifBlank { fromHref?.getOrNull(1).orEmpty() }
    if (id.isBlank() || userId.isBlank() || service.isBlank()) return null

    val footerText = selectFirst("footer.post-card__footer")?.text().orEmpty()

    return PostDomain(
        id = id,
        userId = userId,
        service = service,
        title = selectFirst("header.post-card__header")?.text()?.trim()?.ifBlank { null },
        content = null,
        substring = null,
        added = null,
        /** "2026-07-23 17:54:16" -> "2026-07-23T17:54:16", как в JSON API */
        published = selectFirst("time[datetime]")
            ?.attr("datetime")
            ?.trim()
            ?.ifBlank { null }
            ?.replace(' ', 'T'),
        edited = null,
        file = selectFirst("img.post-card__image")?.attr("src")?.toFileDomain(),
        incompleteRewards = null,
        poll = null,
        /** HTML отдаёт только количество вложений, но не их пути */
        attachments = emptyList(),
        tags = emptyList(),
        nextId = null,
        prevId = null,
        favedSeq = null,
        favCount = FAV_COUNT.find(footerText)?.groupValues?.getOrNull(1)?.toIntOrNullDigits(),
    )
}

private val HREF_POST = Regex("""/([^/]+)/user/([^/]+)/post/([^/?#]+)""")

/**
 * "https://img.pawchive.pw/thumbnail/data/66/5b/665b….png" -> path "/66/5b/665b….png".
 * Храним именно path: превью собирается как "$imgBaseUrl/thumbnail/data$path",
 * поэтому переопределение img-домена в настройках продолжает работать.
 */
private fun String.toFileDomain(): FileDomain? {
    val src = substringBefore('?').substringBefore('#').trim()
    if (src.isBlank()) return null

    val path = when {
        src.contains(THUMBNAIL_PREFIX) -> src.substringAfter(THUMBNAIL_PREFIX)
        src.contains("/data/") -> "/" + src.substringAfter("/data/")
        else -> return null
    }
    if (path.isBlank()) return null

    return FileDomain(name = path.substringAfterLast('/'), path = path)
}

/** Границы периода считаем сами — заголовок на сайте только на английском */
private fun periodRange(period: Period, date: String?): Pair<String, String>? {
    if (period == Period.RECENT) return null
    val day = date?.take(10)?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return null

    val (min, max) = when (period) {
        Period.DAY -> day to day
        Period.WEEK -> day to day.plusDays(6)
        Period.MONTH -> day.withDayOfMonth(1) to day.withDayOfMonth(1).plusMonths(1).minusDays(1)
        Period.RECENT -> return null
    }

    return min.toString() to max.toString()
}

private fun String.toIntOrNullDigits(): Int? =
    filter { it.isDigit() }.takeIf { it.isNotEmpty() }?.toIntOrNull()
