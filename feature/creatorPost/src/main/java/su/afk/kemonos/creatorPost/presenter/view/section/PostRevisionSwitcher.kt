package su.afk.kemonos.creatorPost.presenter.view.section

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R

@Composable
internal fun PostRevisionSwitcher(
    revisionIds: List<Int?>,
    revisionLabel: (Int?) -> String,
    selectedRevisionId: Int?,
    onSelectRevision: (Int?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.post_version_title),
            style = MaterialTheme.typography.titleSmall,
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            revisionIds.forEach { revisionId ->
                val isSelected = revisionId == selectedRevisionId
                val label = revisionLabel(revisionId)

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectRevision(revisionId) },
                    label = { Text(text = label) },
                )
            }
        }
    }
}
