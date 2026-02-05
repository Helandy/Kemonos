package su.afk.kemonos.common.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BaseUrlDomainField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { input ->
            val value = input
                .trim()
                .removePrefix("https://")
                .removePrefix("http://")
                .removeSuffix("/api")
                .removeSuffix("/api/")
                .trim('/')
            onValueChange(value)
        },
        enabled = enabled,
        singleLine = true,
        label = label,
        prefix = { Text("https://") },
        suffix = { Text("/api/") },
    )
}