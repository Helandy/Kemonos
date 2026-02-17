package su.afk.kemonos.ui.components.searchBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import su.afk.kemonos.ui.R

data class SortOption<T>(
    val type: T,
    val label: String,
)

@Composable
fun <T> SearchBarNew(
    query: String,
    onQueryChange: (String) -> Unit,
    services: List<String>,
    selectedService: String,
    onServiceSelect: (String) -> Unit,

    sortOptions: List<SortOption<T>>,
    selectedSort: T,
    onSortMethodSelect: (T) -> Unit,

    isAscending: Boolean,
    onToggleAscending: () -> Unit,

    showRandom: Boolean = false,
    onRandomClick: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current

    var serviceChipWidth by remember { mutableIntStateOf(0) }
    var sortChipWidth by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        /** Поисковая строка */
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text(stringResource(R.string.main_screen_search)) },
                singleLine = true,
                trailingIcon = {
                    Row {
                        if (showRandom && onRandomClick != null) {
                            IconButton(onClick = onRandomClick) {
                                Icon(
                                    imageVector = Icons.Filled.Casino,
                                    contentDescription = stringResource(R.string.random),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(onClick = { onQueryChange(query) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        onQueryChange(query)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            /** Сервис */
            var serviceMenuExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(2f)) {
                FilterChip(
                    selected = true,
                    onClick = { serviceMenuExpanded = true },
                    label = {
                        Text(
                            text = selectedService,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            serviceChipWidth = coordinates.size.width
                        }
                )
                DropdownMenu(
                    expanded = serviceMenuExpanded,
                    onDismissRequest = { serviceMenuExpanded = false },
                    modifier = Modifier.widthIn(
                        min = with(density) { serviceChipWidth.toDp() }
                    )
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = { Text(service) },
                            onClick = {
                                onServiceSelect(service)
                                serviceMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.width(6.dp))

            /** Сортировка */
            var sortMenuExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(2f)) {

                val selectedLabel = sortOptions
                    .firstOrNull { it.type == selectedSort }
                    ?.label
                    ?: sortOptions.firstOrNull()?.label.orEmpty()

                FilterChip(
                    selected = true,
                    onClick = { sortMenuExpanded = true },
                    label = {
                        Text(
                            text = selectedLabel,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            sortChipWidth = coordinates.size.width
                        }
                )

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    modifier = Modifier.widthIn(
                        min = with(density) { sortChipWidth.toDp() }
                    )
                ) {
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                onSortMethodSelect(option.type)
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.width(6.dp))

            /** Реверс */
            FilterChip(
                selected = true,
                onClick = onToggleAscending,
                label = { },
                leadingIcon = {
                    Icon(
                        if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = "Reverse"
                    )
                },
                modifier = Modifier.width(36.dp)
            )
        }
    }
}
