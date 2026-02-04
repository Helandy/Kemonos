package su.afk.kemonos.creatorPost.presenter.view.incompleteRewards

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.models.IncompleteRewards

@Composable
internal fun incompleteRewardsBlock(rewards: IncompleteRewards) {
    val parts = listOfNotNull(
        rewards.mediaCount?.let { "$it media" },
        rewards.photoCount?.let { "$it photos" },
        rewards.videoCount?.let { "$it videos" },
    ).joinToString(", ")

    val priceText = rewards.price?.let { "$$it" } ?: "unknown price"

    Text(
        text = "This post is missing paid rewards from a higher tier.\n\n" +
                "Missing $parts for $priceText.",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
}