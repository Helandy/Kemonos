package su.afk.kemonos.main.presenter.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.ErrorItem
import su.afk.kemonos.main.presenter.StartCheckState.State
import su.afk.kemonos.ui.R
import su.afk.kemonos.ui.components.input.BaseUrlDomainField

@Composable
internal fun MainApiUnavailableContent(
    state: State,
    onInputKemonoChanged: (String) -> Unit,
    onInputCoomerChanged: (String) -> Unit,
    onInputPawchiveChanged: (String) -> Unit,
    onToggleApiSite: (SelectedSite, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainApiUnavailableHeader()

        MainSectionCard(title = stringResource(R.string.main_api_enabled_sites_title)) {
            MainEnabledSitesBlock(
                enabledSites = state.enabledSites,
                onToggleApiSite = onToggleApiSite,
            )
        }

        MainSectionCard(title = stringResource(R.string.main_api_site_status_title)) {
            MainSiteErrorsBlock(
                enabledSites = state.enabledSites,
                kemonoError = state.kemonoError,
                coomerError = state.coomerError,
                pawchiveError = state.pawchiveError,
            )
        }

        MainSectionCard(title = stringResource(R.string.main_api_section_domains_title)) {
            MainDomainFields(
                enabledSites = state.enabledSites,
                inputKemonoDomain = state.inputKemonoDomain,
                inputCoomerDomain = state.inputCoomerDomain,
                inputPawchiveDomain = state.inputPawchiveDomain,
                onInputKemonoChanged = onInputKemonoChanged,
                onInputCoomerChanged = onInputCoomerChanged,
                onInputPawchiveChanged = onInputPawchiveChanged,
            )
        }

        MainSectionCard(title = stringResource(R.string.main_api_current_urls_title)) {
            MainCurrentUrls(
                enabledSites = state.enabledSites,
                kemonoUrl = state.kemonoUrl,
                coomerUrl = state.coomerUrl,
                pawchiveUrl = state.pawchiveUrl,
            )
        }
    }
}

@Composable
private fun MainSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content
            )
        }
    }
}

@Composable
internal fun MainSiteErrorsBlock(
    enabledSites: Set<SelectedSite>,
    kemonoError: ErrorItem?,
    coomerError: ErrorItem?,
    pawchiveError: ErrorItem?,
) {
    val enabledList = enabledSites.orderedSites()

    enabledList.forEachIndexed { index, site ->
        if (index > 0) HorizontalDivider()

        MainSiteStatusRow(
            siteLabel = site.label(),
            error = when (site) {
                SelectedSite.K -> kemonoError
                SelectedSite.C -> coomerError
                SelectedSite.P -> pawchiveError
            }
        )
    }
}

@Composable
private fun MainSiteStatusRow(
    siteLabel: String,
    error: ErrorItem?,
) {
    val isOk = error == null

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = if (isOk)
                stringResource(R.string.main_api_site_available, siteLabel)
            else
                stringResource(R.string.main_api_site_unavailable, siteLabel),
            style = MaterialTheme.typography.titleSmall,
            color = if (isOk) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )

        if (!isOk) {
            MainErrorBlock(error = error)
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
            text = stringResource(R.string.main_api_meta_request_id, it),
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
            text = stringResource(R.string.main_api_meta_cause, error.cause.orEmpty()),
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
internal fun MainCurrentUrls(
    enabledSites: Set<SelectedSite>,
    kemonoUrl: String,
    coomerUrl: String,
    pawchiveUrl: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        enabledSites.orderedSites().forEach { site ->
            Text(
                text = when (site) {
                    SelectedSite.K -> "Kemono: $kemonoUrl"
                    SelectedSite.C -> "Coomer: $coomerUrl"
                    SelectedSite.P -> "Pawchive: $pawchiveUrl"
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun MainEnabledSitesBlock(
    enabledSites: Set<SelectedSite>,
    onToggleApiSite: (SelectedSite, Boolean) -> Unit,
) {
    SelectedSite.entries.forEach { site ->
        val checked = site in enabledSites
        val canToggle = enabledSites.size > 1 || !checked

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = site.label(),
                style = MaterialTheme.typography.bodyLarge,
            )

            Switch(
                checked = checked,
                enabled = canToggle,
                onCheckedChange = { onToggleApiSite(site, it) },
            )
        }
    }
}

@Composable
internal fun MainDomainFields(
    enabledSites: Set<SelectedSite>,
    inputKemonoDomain: String,
    inputCoomerDomain: String,
    inputPawchiveDomain: String,
    onInputKemonoChanged: (String) -> Unit,
    onInputCoomerChanged: (String) -> Unit,
    onInputPawchiveChanged: (String) -> Unit,
) {
    if (SelectedSite.K in enabledSites) {
        BaseUrlDomainField(
            value = inputKemonoDomain,
            onValueChange = onInputKemonoChanged,
            label = { Text(stringResource(R.string.main_api_kemono_url_label)) },
        )
    }

    if (SelectedSite.K in enabledSites && SelectedSite.C in enabledSites) {
        Spacer(Modifier.height(8.dp))
    }

    if (SelectedSite.C in enabledSites) {
        BaseUrlDomainField(
            value = inputCoomerDomain,
            onValueChange = onInputCoomerChanged,
            label = { Text(stringResource(R.string.main_api_coomer_url_label)) },
        )
    }

    if ((SelectedSite.K in enabledSites || SelectedSite.C in enabledSites) && SelectedSite.P in enabledSites) {
        Spacer(Modifier.height(8.dp))
    }

    if (SelectedSite.P in enabledSites) {
        BaseUrlDomainField(
            value = inputPawchiveDomain,
            onValueChange = onInputPawchiveChanged,
            label = { Text(stringResource(R.string.main_api_pawchive_url_label)) },
        )
    }
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
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
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

@Composable
private fun SelectedSite.label(): String =
    when (this) {
        SelectedSite.K -> stringResource(R.string.main_api_kemono_label)
        SelectedSite.C -> stringResource(R.string.main_api_coomer_label)
        SelectedSite.P -> stringResource(R.string.main_api_pawchive_label)
    }

private fun Set<SelectedSite>.orderedSites(): List<SelectedSite> =
    listOf(SelectedSite.K, SelectedSite.C, SelectedSite.P).filter { it in this }
