package su.afk.kemonos.common.presenter.androidView

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import su.afk.kemonos.common.presenter.androidView.model.PostBlock

suspend fun htmlToBlocks(
    html: String,
    baseUrl: String,
): List<PostBlock> {
    if (html.isBlank()) return emptyList()

    val ctx = currentCoroutineContext()

    val doc = Jsoup.parseBodyFragment(html, baseUrl).apply {
        outputSettings().prettyPrint(false)
    }

    fun Element.pickImgUrl(): String {
        val ds = attr("data-src")
        val src = attr("src")
        return resolveUrl(ds.ifBlank { src }, baseUrl)
    }

    fun Element.pickVideoUrl(): String {
        val direct = attr("src")
        if (direct.isNotBlank()) return resolveUrl(direct, baseUrl)

        val source = selectFirst("source[src]")?.attr("src").orEmpty()
        return resolveUrl(source, baseUrl)
    }

    fun Element.pickPoster(): String? {
        val p = attr("poster")
        return resolveUrl(p, baseUrl).takeIf { it.isNotBlank() }
    }

    fun isMediaEl(el: Element): Boolean {
        val tag = el.tagName().lowercase()
        if (tag in setOf("img", "video", "audio", "iframe")) return true
        if (tag == "a" && el.selectFirst("img") != null) return true
        return false
    }

    // Вырезаем медиа из контейнера, сохраняя порядок среди direct-children
    fun extractFromContainer(container: Element): List<PostBlock> {
        val blocks = mutableListOf<PostBlock>()

        val shell = container.clone().empty() // тот же тег/атрибуты, но без детей
        var current = shell.clone().empty()

        fun flushCurrent() {
            val out = current.outerHtml().trim()
            if (out.isNotBlank() && !isEffectivelyEmptyHtml(out)) {
                blocks += PostBlock.Html(out)
            }
            current = shell.clone().empty()
        }

        val childrenSnapshot: List<Node> = container.childNodes().toList()
        for (node in childrenSnapshot) {
            ctx.ensureActive()

            val el = node as? Element
            if (el != null && isMediaEl(el)) {
                flushCurrent()

                when (el.tagName().lowercase()) {
                    "img" -> {
                        val url = el.pickImgUrl()
                        if (url.isNotBlank()) blocks += PostBlock.Image(url)
                    }

                    "a" -> { // <a><img/></a>
                        val img = el.selectFirst("img")
                        val url = img?.pickImgUrl().orEmpty().ifBlank {
                            resolveUrl(el.attr("href"), baseUrl)
                        }
                        if (url.isNotBlank()) blocks += PostBlock.Image(url)
                    }

                    "video" -> {
                        val url = el.pickVideoUrl()
                        if (url.isNotBlank()) blocks += PostBlock.Video(url, el.pickPoster())
                    }

                    "audio" -> {
                        val src = resolveUrl(el.attr("src").ifBlank {
                            el.selectFirst("source[src]")?.attr("src").orEmpty()
                        }, baseUrl)
                        if (src.isNotBlank()) blocks += PostBlock.Audio(src)
                    }

                    else -> {
                        // iframe можно превратить в ссылку (или пропустить)
                    }
                }
            } else {
                // обычный узел — переносим в текущий html-блок
                current.appendChild(node.clone())
            }
        }

        flushCurrent()
        return blocks
    }

    // Нормализуем href/src/poster в оставшемся HTML, чтобы ссылки были абсолютными
    doc.select("[href]").forEach { it.attr("href", resolveUrl(it.attr("href"), baseUrl)) }
    ctx.ensureActive()
    doc.select("[src]").forEach { it.attr("src", resolveUrl(it.attr("src"), baseUrl)) }
    ctx.ensureActive()
    doc.select("[data-src]").forEach { it.attr("data-src", resolveUrl(it.attr("data-src"), baseUrl)) }
    ctx.ensureActive()
    doc.select("[poster]").forEach { it.attr("poster", resolveUrl(it.attr("poster"), baseUrl)) }

    val out = mutableListOf<PostBlock>()

    // идём по top-level элементам, а внутри умеем вытаскивать медиа среди direct-children
    for (top in doc.body().children()) {
        ctx.ensureActive()
        val tag = top.tagName().lowercase()

        when {
            isMediaEl(top) -> {
                // редкий случай: img/video прямо top-level
                out += extractFromContainer(top.parent() ?: top) // на самом деле не должно сюда попадать часто
            }

            tag in setOf("p", "div", "figure", "blockquote", "pre", "ul", "ol", "section") -> {
                out += extractFromContainer(top)
            }

            else -> {
                val h = top.outerHtml().trim()
                if (h.isNotBlank() && !isEffectivelyEmptyHtml(h)) out += PostBlock.Html(h)
            }
        }
    }

    // fallback если body без children (только текст)
    if (out.isEmpty()) {
        val h = doc.body().html().trim()
        if (h.isNotBlank() && !isEffectivelyEmptyHtml(h)) out += PostBlock.Html(h)
    }

    return out
}