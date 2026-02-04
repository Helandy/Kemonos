package su.afk.kemonos.common.view.creator.section

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun CreatorsSectionHeader(
    title: String,
    topSpace: Dp = 0.dp,
    showTopDivider: Boolean = false,
    showBottomDivider: Boolean = true,
    expanded: Boolean? = null,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier = Modifier
        .fillMaxWidth()
        .clickable(enabled = onClick != null) { onClick?.invoke() }

    val rotation by animateFloatAsState(
        targetValue = if (expanded == false) 0f else 180f,
        label = "header_arrow_rotation",
    )

    Column(Modifier.fillMaxWidth()) {
        if (showTopDivider) HorizontalDivider()
        if (topSpace > 0.dp) Spacer(Modifier.height(topSpace))

        Row(
            modifier = clickableModifier.padding(top = 8.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            if (expanded != null) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Rounded.ExpandLess,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation),
                )
            }
        }

        if (showBottomDivider) HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}