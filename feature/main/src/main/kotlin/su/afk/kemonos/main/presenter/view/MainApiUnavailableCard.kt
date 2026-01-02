package su.afk.kemonos.main.presenter.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import su.afk.kemonos.common.R
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.main.presenter.MainState.State

@Composable
internal fun MainApiUnavailableCard(
    state: State,
    onSkipCheck: () -> Unit,
    onSaveAndCheck: () -> Unit,
    onInputKemonoChanged: (String) -> Unit,
    onInputCoomerChanged: (String) -> Unit,
) {
    ElevatedCard(modifier = Modifier.padding(0.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainApiUnavailableHeader()

            MainErrorBlock(error = state.error)

            Spacer(Modifier.height(20.dp))

            MainCurrentUrls(
                kemonoUrl = state.kemonoUrl,
                coomerUrl = state.coomerUrl
            )

            Spacer(Modifier.height(20.dp))

            MainDomainFields(
                inputKemonoDomain = state.inputKemonoDomain,
                inputCoomerDomain = state.inputCoomerDomain,
                onInputKemonoChanged = onInputKemonoChanged,
                onInputCoomerChanged = onInputCoomerChanged,
            )

            Spacer(Modifier.height(20.dp))

            MainActions(
                onSkipCheck = onSkipCheck,
                onSaveAndCheck = onSaveAndCheck,
            )
        }
    }
}


@Composable
internal fun MainApiUnavailableHeader() {
    Text(
        text = stringResource(R.string.main_api_unavailable_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )

    Text(
        text = stringResource(R.string.error_default),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )
}

/** ========== Error UI ========== */

@Composable
internal fun MainErrorBlock(error: ErrorItem?) {
    if (error == null) return

    Spacer(Modifier.height(12.dp))

    MainErrorTitleAndMessage(error)

    val meta = remember(error) { error.toUiMeta() }
    if (meta.hasAny) {
        Spacer(Modifier.height(10.dp))
        MainErrorMeta(meta)
    }

    if (error.hasDebugDetails()) {
        Spacer(Modifier.height(12.dp))
        MainErrorDebugDetails(error)
    }
}

@Composable
internal fun MainErrorTitleAndMessage(error: ErrorItem) {
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
}

@Composable
internal fun MainErrorMeta(meta: ErrorUiMeta) {
    Text(
        text = meta.headline,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center
    )

    meta.url?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }

    meta.requestId?.let {
        Text(
            text = "requestId: $it",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun MainErrorDebugDetails(error: ErrorItem) {
    var expanded by remember { mutableStateOf(false) }

    TextButton(onClick = { expanded = !expanded }) {
        Text(
            if (expanded) stringResource(R.string.hide_details)
            else stringResource(R.string.show_details)
        )
    }

    if (!expanded) return

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
            text = error.body.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

/** ========== URL + Fields + Actions ========== */

@Composable
internal fun MainCurrentUrls(kemonoUrl: String, coomerUrl: String) {
    Text(
        stringResource(R.string.main_api_current_urls_title),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center
    )

    Text(
        text = stringResource(
            R.string.main_api_current_urls_value,
            kemonoUrl,
            coomerUrl
        ),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun MainDomainFields(
    inputKemonoDomain: String,
    inputCoomerDomain: String,
    onInputKemonoChanged: (String) -> Unit,
    onInputCoomerChanged: (String) -> Unit,
) {
    BaseUrlDomainField(
        value = inputKemonoDomain,
        onValueChange = onInputKemonoChanged,
        label = { Text(stringResource(R.string.main_api_kemono_url_label)) },
    )

    Spacer(Modifier.height(8.dp))

    BaseUrlDomainField(
        value = inputCoomerDomain,
        onValueChange = onInputCoomerChanged,
        label = { Text(stringResource(R.string.main_api_coomer_url_label)) },
    )
}

@Composable
internal fun MainActions(
    onSkipCheck: () -> Unit,
    onSaveAndCheck: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onSkipCheck,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.main_api_skip_check_button))
        }

        Button(
            onClick = onSaveAndCheck,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.main_api_check_button))
        }
    }
}

/** ========== Pure helpers ========== */

internal data class ErrorUiMeta(
    val hasAny: Boolean,
    val headline: String,
    val url: String?,
    val requestId: String?,
)

internal fun ErrorItem.toUiMeta(): ErrorUiMeta {
    val code = this.code
    val method = this.method?.takeIf { it.isNotBlank() }
    val url = this.url?.takeIf { it.isNotBlank() }
    val requestId = this.requestId?.takeIf { it.isNotBlank() }

    val headline = buildString {
        if (code != null) append("HTTP $code")
        if (!method.isNullOrBlank()) {
            if (isNotEmpty()) append(" • ")
            append(method)
        }
    }

    val hasAny = code != null || method != null || url != null || requestId != null

    return ErrorUiMeta(
        hasAny = hasAny,
        headline = headline,
        url = url,
        requestId = requestId,
    )
}

internal fun ErrorItem.hasDebugDetails(): Boolean =
    !body.isNullOrBlank() || !cause.isNullOrBlank()