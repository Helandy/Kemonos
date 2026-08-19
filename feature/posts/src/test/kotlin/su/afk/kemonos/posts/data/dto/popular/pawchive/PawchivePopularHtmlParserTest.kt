package su.afk.kemonos.posts.data.dto.popular.pawchive

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.presenter.pagePopularPosts.utils.tripleFor

class PawchivePopularHtmlParserTest {

    @Test
    fun `parses posts from popular page`() {
        val result = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.MONTH,
            requestedDate = "2026-07-01",
            offset = 450,
        )

        assertEquals(3, result.posts.size)

        val first = result.posts[0]
        assertEquals("164656869", first.id)
        assertEquals("14258012", first.userId)
        assertEquals("patreon", first.service)
        assertEquals("Hatsune Miku Fox", first.title)
        assertEquals("2026-07-23T17:54:16", first.published)
        assertEquals(149, first.favCount)
        assertEquals(
            "/66/5b/665b9a48ccb28988651bbf75d06abc131a255631bb88926f4cc5e4afd99367ef.png",
            first.file?.path,
        )
        assertEquals(
            "665b9a48ccb28988651bbf75d06abc131a255631bb88926f4cc5e4afd99367ef.png",
            first.file?.name,
        )
    }

    @Test
    fun `keeps post without cover image`() {
        val withoutCover = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.MONTH,
            requestedDate = "2026-07-01",
            offset = 450,
        ).posts[1]

        assertEquals("149912585", withoutCover.id)
        assertNull(withoutCover.file)
        assertEquals(515, withoutCover.favCount)
    }

    @Test
    fun `falls back to link when data attributes are missing`() {
        val fromHref = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.MONTH,
            requestedDate = "2026-07-01",
            offset = 450,
        ).posts[2]

        assertEquals("12305228", fromHref.id)
        assertEquals("37736420", fromHref.userId)
        assertEquals("fanbox", fromHref.service)
    }

    @Test
    fun `reads total count from paginator`() {
        val result = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.MONTH,
            requestedDate = "2026-07-01",
            offset = 450,
        )

        assertEquals(500, result.props.count)
    }

    @Test
    fun `maps date navigation into prev current next`() {
        val info = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.MONTH,
            requestedDate = "2026-07-01",
            offset = 450,
        ).info

        val nav = info.navigationDates

        assertEquals(Triple("2026-06-30", "2026-07-01", "2026-07-02"), nav.tripleFor(Period.DAY))
        assertEquals(Triple("2026-06-23", "2026-06-30", "2026-07-07"), nav.tripleFor(Period.WEEK))
        /** активный период не ссылается сам на себя — центр берётся из запроса */
        assertEquals(Triple("2026-06-01", "2026-07-01", null), nav.tripleFor(Period.MONTH))
        assertNull(nav.tripleFor(Period.RECENT))
    }

    @Test
    fun `today allows navigation forward only while site offers next link`() {
        val props = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.MONTH,
            requestedDate = "2026-07-01",
            offset = 450,
        ).props

        /** максимум из дат страницы: next по дню есть, next по месяцу — нет */
        assertEquals("2026-07-07", props.today)
    }

    @Test
    fun `computes month range locally`() {
        val info = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.MONTH,
            requestedDate = "2026-07-01",
            offset = 450,
        ).info

        assertEquals("2026-07-01", info.minDate)
        assertEquals("2026-07-31", info.maxDate)
        assertEquals("month", info.scale)
    }

    @Test
    fun `recent period has no range`() {
        val info = parsePawchivePopularHtml(
            html = popularHtml(),
            period = Period.RECENT,
            requestedDate = null,
            offset = 0,
        ).info

        assertNull(info.minDate)
        assertNull(info.maxDate)
    }

    @Test
    fun `throws when page is not the popular page`() {
        assertThrows(IllegalStateException::class.java) {
            parsePawchivePopularHtml(
                html = "<html><body><h1>Just a moment…</h1></body></html>",
                period = Period.MONTH,
                requestedDate = "2026-07-01",
                offset = 0,
            )
        }
    }

    private fun popularHtml(): String = """
        <html>
          <head><meta name="count" content="500"></head>
          <body>
            <div class="paginator" id="paginator-dates">
              <div id="daily">
                <span><a href="/posts/popular?date=2026-06-30&period=day">&laquo; prev</a></span>
                <span><a href="/posts/popular?date=2026-07-01&period=day">Day</a></span>
                <span><a href="/posts/popular?date=2026-07-02&period=day">next &raquo;</a></span>
              </div>
              <div id="weekly">
                <span><a href="/posts/popular?date=2026-06-23&period=week">&laquo; prev</a></span>
                <span><a href="/posts/popular?date=2026-06-30&period=week">Week</a></span>
                <span><a href="/posts/popular?date=2026-07-07&period=week">next &raquo;</a></span>
              </div>
              <div id="monthly">
                <span><a href="/posts/popular?date=2026-06-01&period=month">&laquo; prev</a></span>
                <span><strong>Month</strong></span>
                <span><span>next &raquo;</span></span>
              </div>
              <div class="paginator" id="paginator-top">
                <small>Showing 451 - 500 of 500</small>
              </div>
            </div>

            <article class="post-card post-card--preview" data-id="164656869" data-service="patreon" data-user="14258012">
              <a href="/patreon/user/14258012/post/164656869">
                <header class="post-card__header">Hatsune Miku Fox</header>
                <div class="post-card__image-container">
                  <img class="post-card__image" src="https://img.pawchive.pw/thumbnail/data/66/5b/665b9a48ccb28988651bbf75d06abc131a255631bb88926f4cc5e4afd99367ef.png">
                </div>
                <footer class="post-card__footer">
                  <time class="timestamp" datetime="2026-07-23 17:54:16">2026-07-23 17:54:16</time>
                  <div>6 attachments<br>149 favorites</div>
                  <img src="/static/small_icons/patreon.png">
                </footer>
              </a>
            </article>

            <article class="post-card post-card--preview" data-id="149912585" data-service="patreon" data-user="5564244">
              <a href="/patreon/user/5564244/post/149912585">
                <header class="post-card__header">Links to All My Animations</header>
                <footer class="post-card__footer">
                  <time class="timestamp" datetime="2026-02-04 11:29:36">2026-02-04 11:29:36</time>
                  <div>No attachments<br>515 favorites</div>
                  <img src="/static/small_icons/patreon.png">
                </footer>
              </a>
            </article>

            <article class="post-card post-card--preview">
              <a href="/fanbox/user/37736420/post/12305228">
                <header class="post-card__header">New Animation Link</header>
                <div class="post-card__image-container">
                  <img class="post-card__image" src="https://img.pawchive.pw/thumbnail/data/35/16/35162a72190362b2b61232d1ee3f2052f03f18492c528a75c86c63a2ea53f430.jpeg">
                </div>
                <footer class="post-card__footer">
                  <time class="timestamp" datetime="2026-07-25 08:45:27">2026-07-25 08:45:27</time>
                  <div>3 attachments<br>927 favorites</div>
                </footer>
              </a>
            </article>
          </body>
        </html>
    """.trimIndent()
}
