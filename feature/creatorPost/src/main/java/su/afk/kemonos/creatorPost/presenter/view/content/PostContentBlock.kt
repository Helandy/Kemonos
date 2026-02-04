package su.afk.kemonos.creatorPost.presenter.view.content

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import su.afk.kemonos.common.imageLoader.videoPreview.MediaWithAutoRatio
import su.afk.kemonos.common.presenter.androidView.HtmlTextBlock
import su.afk.kemonos.common.presenter.androidView.model.PostBlock

internal fun LazyListScope.postContentSection(
    blocks: List<PostBlock>,
    onOpenImage: (String) -> Unit,
) {
    items(
        count = blocks.size,
        key = { key ->
            when (val b = blocks[key]) {
                is PostBlock.Html -> "html:$key:${b.html.take(32).hashCode()}"
                is PostBlock.Image -> "img:${b.url}"
                is PostBlock.Video -> "vid:${b.url}"
                is PostBlock.Audio -> "aud:${b.url}"
            }
        }
    ) { item ->
        val context = LocalContext.current

        when (val block = blocks[item]) {
            is PostBlock.Html -> HtmlTextBlock(
                html = block.html,
                modifier = Modifier.fillMaxWidth(),
                onOpenUrl = { url ->
                    runCatching {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, url.toUri())
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
            )

            is PostBlock.Image -> MediaWithAutoRatio(
                model = block.url,
                key = "img:${block.url}",
                onClick = { onOpenImage(block.url) }
            )

            is PostBlock.Video -> {
                val poster = block.poster
                if (!poster.isNullOrBlank()) {
                    MediaWithAutoRatio(
                        model = poster,
                        key = "vidPoster:$poster",
                        onClick = { onOpenImage(block.url) }
                    )
                } else {
                    Spacer(Modifier.height(8.dp))
                }
            }

            is PostBlock.Audio -> Spacer(Modifier.height(8.dp))
        }
    }
}