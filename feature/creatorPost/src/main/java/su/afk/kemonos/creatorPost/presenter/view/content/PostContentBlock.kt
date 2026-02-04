package su.afk.kemonos.creatorPost.presenter.view.content

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import su.afk.kemonos.common.imageLoader.videoPreview.MediaWithAutoRatio
import su.afk.kemonos.common.presenter.androidView.HtmlTextBlock
import su.afk.kemonos.common.presenter.androidView.model.PostBlock

@Composable
internal fun PostContentBlock(
    blocks: List<PostBlock>?,
    onOpenImage: (String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth().padding(4.dp)
    ) {
        val list = blocks.orEmpty()
        if (list.isEmpty()) return@Column

        list.forEach { block ->
            when (block) {
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

                is PostBlock.Image -> {
                    MediaWithAutoRatio(
                        model = block.url,
                        key = "img:${block.url}",
                        onClick = { onOpenImage(block.url) }
                    )
                }

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
}