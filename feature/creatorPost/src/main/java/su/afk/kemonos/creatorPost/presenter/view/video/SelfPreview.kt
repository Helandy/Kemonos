package su.afk.kemonos.creatorPost.presenter.view.video

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
internal fun SelfPreview(
    showPreview: Boolean,
    url: String,
    videoPath: String,
    contentDescription: String,
    blurImage: Boolean,
    context: Context,
    onLoadingChanged: (Boolean) -> Unit,
) {
    LaunchedEffect(url, videoPath, showPreview, context) {
        onLoadingChanged(false)
    }

    Box(
        Modifier.fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}
