package su.afk.kemonos.presenter.main

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.R
import su.afk.kemonos.common.presenter.updateBanner.UpdateBanner

@Composable
internal fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val updateInfo = state.updateInfo

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainEffect.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            updateInfo != null -> {
                UpdateBanner(
                    info = updateInfo,
                    onUpdateClick = { viewModel.onUpdateClick(updateInfo) },
                    onLaterClick = { viewModel.onUpdateLaterClick() }
                )
            }

            state.isLoading -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.main_checking_api))
            }

            state.apiSuccess == true -> {
                Text(stringResource(R.string.main_checking_api_success))
            }

            else -> {
                ElevatedCard(
                    modifier = Modifier.padding(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = stringResource(R.string.main_api_unavailable_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )

                        val error = state.error
                        if (error != null) {
                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = error.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = error.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            val code = error.code
                            val method = error.method
                            val url = error.url
                            val requestId = error.requestId

                            if (code != null || !method.isNullOrBlank() || !url.isNullOrBlank() || !requestId.isNullOrBlank()) {
                                Spacer(Modifier.height(10.dp))

                                Text(
                                    text = buildString {
                                        if (code != null) append("HTTP $code")
                                        if (!method.isNullOrBlank()) {
                                            if (isNotEmpty()) append(" • ")
                                            append(method)
                                        }
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center
                                )

                                if (!url.isNullOrBlank()) {
                                    Text(
                                        text = url,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (!requestId.isNullOrBlank()) {
                                    Text(
                                        text = "requestId: $requestId",
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            val hasDebug = !error.body.isNullOrBlank() || !error.cause.isNullOrBlank()
                            if (hasDebug) {
                                Spacer(Modifier.height(12.dp))
                                var expanded by remember { mutableStateOf(false) }

                                TextButton(onClick = { expanded = !expanded }) {
                                    Text(
                                        if (expanded)
                                            stringResource(R.string.hide_details)
                                        else
                                            stringResource(R.string.show_details)
                                    )
                                }

                                if (expanded) {
                                    if (!error.cause.isNullOrBlank()) {
                                        Text(
                                            text = "cause: ${error.cause}",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(Modifier.height(6.dp))
                                    }
                                    if (!error.body.isNullOrBlank()) {
                                        Text(
                                            text = error.body ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            stringResource(R.string.main_api_current_urls_title),
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(
                                R.string.main_api_current_urls_value,
                                state.kemonoUrl,
                                state.coomerUrl
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(20.dp))

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.inputKemono,
                            onValueChange = viewModel::onInputKemonoChanged,
                            label = { Text(stringResource(R.string.main_api_kemono_url_label)) },
                            singleLine = true
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.inputCoomer,
                            onValueChange = viewModel::onInputCoomerChanged,
                            label = { Text(stringResource(R.string.main_api_coomer_url_label)) },
                            singleLine = true
                        )

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = viewModel::onSkipCheck,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.main_api_skip_check_button))
                            }
                            Button(
                                onClick = viewModel::onSaveAndCheck,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.main_api_check_button))
                            }
                        }
                    }
                }
            }
        }
    }
}