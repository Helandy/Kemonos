package su.afk.kemonos.ui.components.updateBanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import su.afk.kemonos.app.update.api.model.AppUpdateInfo
import su.afk.kemonos.ui.R

@Composable
fun UpdateBanner(
    info: AppUpdateInfo,
    onUpdateClick: () -> Unit,
    onLaterClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            /** Заголовок */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SystemUpdateAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(
                            R.string.update_banner_title,
                            info.latestVersionName
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = stringResource(R.string.update_banner_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                /** Кнопка раскрытия changelog */
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            /** Changelog: Markdown */
            AnimatedVisibility(expanded) {
                Column(Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(
                        Modifier,
                        DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                    Spacer(Modifier.height(12.dp))

                    /** Внутренняя прокрутка changelog */
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(4.dp)
                    ) {
                        Markdown(
                            content = info.changelog,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            /** Позже - Обновить */
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onLaterClick
                ) {
                    Text(stringResource(R.string.update_btn_later))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onUpdateClick
                ) {
                    Text(stringResource(R.string.update_btn_update))
                }
            }
        }
    }
}
