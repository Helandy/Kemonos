package su.afk.kemonos.creatorPost.presenter.view.translate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.common.util.toUiDateTime

@Composable
internal fun PostTranslateItem(
    published: String?,
    edited: String?,
    added: String?,
    rawHtml: String,

    expanded: Boolean,
    loading: Boolean,
    translated: String?,
    error: String?,

    onToggleTranslate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateText = remember(published, edited) {
                listOfNotNull(
                    published?.let { "📅 ${it.toUiDateTime()}" },
                    edited?.takeIf { !it.isNullOrBlank() && it != published }?.let { "✏️ ${it.toUiDateTime()}" }
                ).joinToString(" / ")
            }

            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                enabled = !loading,
                onClick = onToggleTranslate
            ) {
                if (loading) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.translate_in_progress))
                } else {
                    Text(stringResource(if (expanded) R.string.translate_hide else R.string.translate_show))
                }
            }
        }

        AnimatedVisibility(visible = expanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(stringResource(R.string.translate_title), style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))

                    when {
                        loading -> Text(
                            stringResource(R.string.translate_model_loading),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        error != null -> Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )

                        translated != null -> Text(
                            translated,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
