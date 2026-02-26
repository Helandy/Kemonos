package su.afk.kemonos.setting.presenter.view.uiSetting.translate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import su.afk.kemonos.profile.R
import java.util.*

private data class LangOption(
    val tag: String,
    val label: String,
)

private val TOP_30_LANGUAGES = listOf(
    LangOption("en", "English"),
    LangOption("ru", "Русский"),
    LangOption("es", "Español"),
    LangOption("pt", "Português"),
    LangOption("fr", "Français"),
    LangOption("de", "Deutsch"),
    LangOption("it", "Italiano"),
    LangOption("pl", "Polski"),
    LangOption("tr", "Türkçe"),
    LangOption("uk", "Українська"),
    LangOption("cs", "Čeština"),
    LangOption("sk", "Slovenčina"),
    LangOption("ro", "Română"),
    LangOption("hu", "Magyar"),
    LangOption("nl", "Nederlands"),
    LangOption("sv", "Svenska"),
    LangOption("fi", "Suomi"),
    LangOption("da", "Dansk"),
    LangOption("no", "Norsk"),
    LangOption("el", "Ελληνικά"),
    LangOption("bg", "Български"),
    LangOption("sr", "Српски"),
    LangOption("hr", "Hrvatski"),
    LangOption("sl", "Slovenščina"),
    LangOption("lt", "Lietuvių"),
    LangOption("lv", "Latviešu"),
    LangOption("et", "Eesti"),
    LangOption("id", "Bahasa Indonesia"),
    LangOption("vi", "Tiếng Việt"),
    LangOption("th", "ไทย"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TranslateLanguageRow(
    title: String,
    languageTag: String,          // "" = system
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    val isSystem = languageTag.isBlank()
    val systemLang = remember { Locale.getDefault().language }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Box {
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = isSystem,
                    onClick = { onChange("") },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    label = {
                        Text(stringResource(R.string.settings_translate_language_system))
                    }
                )
                SegmentedButton(
                    selected = !isSystem,
                    onClick = { expanded = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    label = {
                        Text(stringResource(R.string.settings_translate_language_choose))
                    }
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 12.dp, y = 0.dp)
            ) {
                TOP_30_LANGUAGES.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang.label) },
                        onClick = {
                            onChange(lang.tag)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    // аккуратный hint под строкой
    Text(
        text = if (isSystem) {
            stringResource(
                R.string.settings_translate_language_system_hint,
                systemLang
            )
        } else {
            stringResource(
                R.string.settings_translate_language_current,
                languageTag
            )
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )

    Divider()
}