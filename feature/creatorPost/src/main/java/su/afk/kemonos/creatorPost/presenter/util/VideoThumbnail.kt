package su.afk.kemonos.creatorPost.presenter.util

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VideoThumbnail(
    url: String,
    modifier: Modifier = Modifier,
    onLoadingChange: (Boolean) -> Unit = {},
) {
    var bitmap by remember(url) { mutableStateOf<Bitmap?>(null) }
    var loading by remember(url) { mutableStateOf(true) }
    var error by remember(url) { mutableStateOf<Throwable?>(null) }

    LaunchedEffect(url) {
        loading = true
        error = null
        bitmap = null
        onLoadingChange(true)

        val result = withContext(Dispatchers.IO) {
            runCatching { getVideoFrame(url, timeUs = 1_000_000L) }
        }

        result
            .onSuccess { bmp -> bitmap = bmp }
            .onFailure { t -> error = t }
            .also {
                loading = false
                onLoadingChange(false)
            }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            loading -> Unit

            else -> {
                // error placeholder
            }
        }
    }
}