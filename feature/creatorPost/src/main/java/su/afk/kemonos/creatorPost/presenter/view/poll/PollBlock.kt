package su.afk.kemonos.creatorPost.presenter.view.poll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.util.toUiDateTime
import su.afk.kemonos.domain.models.PollDomain
import su.afk.kemonos.preferences.ui.DateFormatMode
import kotlin.math.roundToInt

@Composable
internal fun PollBlock(
    poll: PollDomain,
    dateMode: DateFormatMode,
) {
    val choices = poll.choices
    if (choices.isNullOrEmpty()) return

    val totalVotes = choices.sumOf { (it.votes ?: 0).coerceAtLeast(0) }.coerceAtLeast(1)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = poll.title?.takeIf { it.isNotBlank() } ?: "Poll",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (poll.allowsMultiple) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Multiple") },
                        enabled = false,
                    )
                }
            }

            val desc = poll.description?.trim().orEmpty()
            if (desc.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.height(10.dp))

            choices.forEachIndexed { idx, c ->
                val votes = (c.votes ?: 0).coerceAtLeast(0)
                val fraction = votes.toFloat() / totalVotes.toFloat()
                val percent = (fraction * 100f).roundToInt()

                ChoiceRow(
                    index = idx,
                    text = c.text?.takeIf { it.isNotBlank() } ?: "Option ${idx + 1}",
                    votes = votes,
                    percent = percent,
                    progress = fraction.coerceIn(0f, 1f),
                )

                if (idx != choices.lastIndex) Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val created = poll.createdAt?.takeIf { it.isNotBlank() }
                val closes = poll.closesAt?.takeIf { it.isNotBlank() }

                Text(
                    text = created?.let { "Created: ${it.toUiDateTime(dateMode)}" } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = closes?.let { "Closes: ${it.toUiDateTime(dateMode)}" } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ChoiceRow(
    index: Int,
    text: String,
    votes: Int,
    percent: Int,
    progress: Float,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "$votes • $percent%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )
    }
}