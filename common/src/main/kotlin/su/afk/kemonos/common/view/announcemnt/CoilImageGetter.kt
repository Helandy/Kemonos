package su.afk.kemonos.common.view.announcemnt

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.ViewTreeObserver
import android.widget.TextView
import coil3.Image
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

/**
 * Html.ImageGetter для TextView, который:
 *
 * - Загружает <img> через Coil
 * - Масштабирует изображения под ширину текста
 * - Не вызывает relayout во время скролла LazyColumn
 * - Предотвращает скачки скролла при догрузке изображений
 *
 * Используется совместно с HtmlCompat.fromHtml(...)
 */
class CoilImageGetter(
    /** TextView, в который вставляется HTML */
    private val textView: TextView,

    /** Общий ImageLoader (желательно один на экран / DI) */
    private val imageLoader: ImageLoader,

    /** CoroutineScope из Compose (жизненный цикл карточки) */
    private val scope: CoroutineScope,

    /** Флаг, идёт ли сейчас скролл LazyColumn */
    private val isScrolling: () -> Boolean,
) : Html.ImageGetter {

    /**
     * Placeholder'ы по url.
     *
     * HtmlCompat может вызвать getDrawable() несколько раз для одного <img>,
     * поэтому важно всегда возвращать один и тот же Drawable.
     */
    private val placeholders = ConcurrentHashMap<String, ResizableDrawable>()

    /**
     * Флаг для схлопывания нескольких requestLayout в один.
     *
     * Без этого при загрузке нескольких картинок подряд будут
     * постоянные пересчёты высоты и дёргание UI.
     */
    private val relayoutScheduled = AtomicBoolean(false)

    /**
     * Вызывается HtmlCompat при встрече <img src="...">
     *
     * Обязан вернуть Drawable СРАЗУ, поэтому сначала отдаём placeholder,
     * а реальную картинку подгружаем асинхронно.
     */
    override fun getDrawable(source: String?): Drawable {
        val raw = source.orEmpty().trim()
        if (raw.isBlank()) return ColorDrawable(Color.TRANSPARENT)

        /** Нормализуем url */
        val url = if (raw.startsWith("//")) "https:$raw" else raw

        /** Если placeholder уже создан — возвращаем его */
        placeholders[url]?.let { return it }

        /** Создаём прозрачный placeholder минимального размера */
        val placeholder = ResizableDrawable(ColorDrawable(Color.TRANSPARENT)).apply {
            setBounds(0, 0, 1, dp(textView, 1))
        }

        /** Гарантируем, что placeholder один на url */
        val prev = placeholders.putIfAbsent(url, placeholder)
        if (prev != null) return prev

        /** Асинхронно загружаем картинку */
        scope.launch {
            val tvWidth = awaitTextWidth(textView)
            if (tvWidth <= 1) return@launch

            val image = runCatching { loadImage(url) }.getOrNull() ?: return@launch
            val drawable = CoilImageDrawable(image)

            /** Масштабируем изображение под ширину текста */
            val (w, h) = fitSize(
                srcW = image.width.coerceAtLeast(1),
                srcH = image.height.coerceAtLeast(1),
                maxW = tvWidth
            )

            textView.post {
                drawable.setBounds(0, 0, w, h)
                placeholder.setBounds(0, 0, w, h)
                placeholder.setInner(drawable)

                /**
                 * Не делаем setText сразу —
                 * сначала ждём, пока пользователь перестанет скроллить
                 */
                requestRelayoutWhenIdle()
            }
        }

        return placeholder
    }

    /**
     * Выполняет пересчёт спанов и высоты TextView
     * ТОЛЬКО когда скролл остановился.
     *
     * Это ключевое место, убирающее скачки LazyColumn.
     */
    private fun requestRelayoutWhenIdle() {
        if (!relayoutScheduled.compareAndSet(false, true)) return

        textView.post {
            relayoutScheduled.set(false)

            if (isScrolling()) {
                /** Пока скроллится — откладываем */
                textView.postDelayed({ requestRelayoutWhenIdle() }, 80L)
                return@post
            }

            /** Скролл остановился — можно безопасно пересчитать layout */
            textView.setText(textView.text, TextView.BufferType.SPANNABLE)
            textView.invalidate()
            textView.requestLayout()
        }
    }

    /**
     * Загружает Image через Coil с включёнными кэшами
     */
    private suspend fun loadImage(url: String): Image? = withContext(Dispatchers.IO) {
        val req = ImageRequest.Builder(textView.context)
            .data(url)
            .allowHardware(false) // TextView не дружит с hardware bitmap
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build()

        val result = imageLoader.execute(req)
        (result as? SuccessResult)?.image
    }

    /**
     * Ожидает, пока TextView будет измерен,
     * чтобы корректно масштабировать картинку
     */
    private suspend fun awaitTextWidth(tv: TextView): Int {
        val now = textWidth(tv)
        if (now > 1) return now

        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val vto = tv.viewTreeObserver
                val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val w = textWidth(tv)
                        if (w > 1) {
                            if (vto.isAlive) vto.removeOnGlobalLayoutListener(this)
                            if (cont.isActive) cont.resume(w)
                        }
                    }
                }
                vto.addOnGlobalLayoutListener(listener)
                cont.invokeOnCancellation {
                    if (vto.isAlive) vto.removeOnGlobalLayoutListener(listener)
                }
            }
        }
    }

    /** Фактическая ширина текста без паддингов */
    private fun textWidth(tv: TextView): Int =
        (tv.width - tv.paddingLeft - tv.paddingRight).coerceAtLeast(0)

    /**
     * Масштабирует изображение, сохраняя пропорции,
     * чтобы оно влезло в ширину текста
     */
    private fun fitSize(srcW: Int, srcH: Int, maxW: Int): Pair<Int, Int> {
        if (srcW <= maxW) return srcW to srcH
        val ratio = maxW.toFloat() / srcW.toFloat()
        return maxW to (srcH * ratio).toInt().coerceAtLeast(1)
    }
}

/**
 * Drawable-обёртка:
 * сначала рисует placeholder,
 * потом подменяется на реальный Drawable
 */
private class ResizableDrawable(
    private var inner: Drawable
) : Drawable() {

    /** Подменяет внутренний drawable */
    fun setInner(d: Drawable) {
        inner = d
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        inner.bounds = bounds
        inner.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        inner.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        inner.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = inner.opacity
}

/**
 * Drawable, который напрямую рисует coil3.Image
 * без промежуточного Bitmap.
 */
private class CoilImageDrawable(
    private val image: Image
) : Drawable() {

    override fun draw(canvas: Canvas) {
        val b: Rect = bounds
        if (b.isEmpty) return

        val iw = image.width.coerceAtLeast(1)
        val ih = image.height.coerceAtLeast(1)

        val save = canvas.save()
        canvas.translate(b.left.toFloat(), b.top.toFloat())
        canvas.scale(
            b.width().toFloat() / iw.toFloat(),
            b.height().toFloat() / ih.toFloat()
        )
        image.draw(canvas)
        canvas.restoreToCount(save)
    }

    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = image.width
    override fun getIntrinsicHeight(): Int = image.height
}

/**
 * Конвертация dp -> px
 */
private fun dp(tv: TextView, dp: Int): Int {
    val density = tv.resources.displayMetrics.density
    return (dp * density).toInt().coerceAtLeast(1)
}