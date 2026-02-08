package su.afk.kemonos.common.error.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.domain.models.ErrorItem

@Composable
fun DefaultErrorContent(
    errorItem: ErrorItem,
    onRetry: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    Log.e("super", "ErrorContent: $errorItem")
    val context = LocalContext.current
    var showDetails by remember { mutableStateOf(false) }

    val copyText = remember(errorItem) { errorItem.toClipboardText() }

    val hasDetails = remember(errorItem) {
        errorItem.code != null ||
                errorItem.method != null ||
                errorItem.url != null ||
                errorItem.requestId != null ||
                (errorItem.body?.isNotBlank() == true) ||
                (errorItem.cause?.isNotBlank() == true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = errorItem.title.ifBlank { stringResource(R.string.error_screen) },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = errorItem.message,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(Modifier.height(16.dp))

            /** КНОПКИ: Назад / Детали / Повторить */
            if (onBack == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (hasDetails) {
                        OutlinedButton(
                            onClick = { showDetails = !showDetails },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (showDetails) stringResource(R.string.hide_details)
                                else stringResource(R.string.details)
                            )
                        }
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.retry))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(stringResource(R.string.back))
                    }

                    if (hasDetails) {
                        TextButton(
                            onClick = { showDetails = !showDetails },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                if (showDetails) stringResource(R.string.hide_details)
                                else stringResource(R.string.details)
                            )
                        }
                    }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }

            /** ДЕТАЛИ */
            AnimatedVisibility(visible = showDetails && hasDetails) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    errorItem.code?.let { Text(stringResource(R.string.error_http_code, it.toString())) }
                    errorItem.method?.let { Text(stringResource(R.string.error_method, it)) }
                    errorItem.url?.let { Text(stringResource(R.string.error_url, it)) }
                    errorItem.requestId?.let { Text(stringResource(R.string.error_request_id, it)) }

                    errorItem.cause?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(text = it, style = MaterialTheme.typography.bodySmall)
                    }

                    errorItem.body?.takeIf { it.isNotBlank() }?.let { body ->
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.error_body, body),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    /** Copy показываем ТОЛЬКО когда раскрыты детали */
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { context.copyToClipboard(copyText) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(stringResource(R.string.copy))
                    }
                }
            }
        }
    }
}

private fun ErrorItem.toClipboardText(): String {
    val lines = buildList {
        add("Title: $title")
        add("Message: $message")
        code?.let { add("HTTP: $it") }
        method?.let { add("Method: $it") }
        url?.let { add("URL: $it") }
        requestId?.let { add("RequestId: $it") }
        cause?.let { add("Cause: $it") }
        body?.takeIf { it.isNotBlank() }?.let {
            add("")
            add("Body:")
            add(it)
        }
    }
    return lines.joinToString("\n")
}

private fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("error", text))
}