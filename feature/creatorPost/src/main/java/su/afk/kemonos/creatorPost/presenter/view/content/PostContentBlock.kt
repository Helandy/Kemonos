package su.afk.kemonos.creatorPost.presenter.view.content

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.presenter.androidView.HtmlTextBlock
import su.afk.kemonos.common.presenter.androidView.htmlToBlocks
import su.afk.kemonos.common.presenter.androidView.isEffectivelyEmptyHtml
import su.afk.kemonos.common.presenter.androidView.model.PostBlock

@Composable
internal fun PostContentBlock(
    service: String,
    body: String,
    onOpenImage: (String) -> Unit,
) {
    if (body.isBlank()) return
    if (remember(body) { isEffectivelyEmptyHtml(body) }) return

    val context = LocalContext.current
    val resolver = LocalDomainResolver.current
    val siteBaseUrl = remember(service) { resolver.baseUrlByService(service) }

    val blocks by produceState<List<PostBlock>>(initialValue = emptyList(), body, siteBaseUrl) {
        value = withContext(Dispatchers.Default) {
            htmlToBlocks(body, siteBaseUrl)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is PostBlock.Html -> {
                    HtmlTextBlock(
                        html = block.html,
                        modifier = Modifier.fillMaxWidth(),
                        onOpenUrl = { url ->
                            val uri = url.toUri()

                            // тут можешь распознавать “файлы/видео/аудио” и открывать внутри
                            runCatching {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        }
                    )
                }

                is PostBlock.Image -> {
                    AsyncImage(
                        model = block.url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onOpenImage(block.url) }
                    )
                }

                is PostBlock.Video -> {
                    // минимум: кликабельный постер (или любой плейсхолдер)
                    val poster = block.poster
                    if (!poster.isNullOrBlank()) {
                        AsyncImage(
                            model = poster,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            block.url.toUri()
                                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                        )
                    } else {
                        // если постера нет — можно рисовать простую карточку “Play”
                        Spacer(Modifier.height(8.dp))
                    }
                }

                is PostBlock.Audio -> {
                    // аналогично — строка/карточка аудио
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}