package su.afk.kemonos.profile.presenter.setting.view.uiSetting.download

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import su.afk.kemonos.preferences.ui.DownloadFolderMode
import su.afk.kemonos.utils.download.normalizeForFolder

private data class PathExample(
    val template: String,
    val example: String,
)

@Composable
internal fun DownloadFolderModeRow(
    title: String,
    value: DownloadFolderMode,
    addServiceName: Boolean,
    onChange: (DownloadFolderMode) -> Unit,
) {
    val examples = buildDownloadFolderExamples(addServiceName)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(8.dp))

        DownloadFolderMode.entries.forEachIndexed { index, mode ->
            val ex = examples.getValue(mode)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChange(mode) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = value == mode,
                    onClick = { onChange(mode) }
                )

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Шаблон
                    Text(
                        text = ex.template,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                    )

                    Spacer(Modifier.height(2.dp))

                    // Пример
                    Text(
                        text = ex.example,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }

            if (index != DownloadFolderMode.entries.lastIndex) {
                Divider()
            }
        }
    }
}

private fun buildDownloadFolderExamples(addServiceName: Boolean): Map<DownloadFolderMode, PathExample> {
    val service = "patreon"
    val creator = "SomeCreator"
    val postId = "146317720"
    val postTitle = "My awesome post"

    val templatePrefix = if (addServiceName) "<service>/" else ""
    val examplePrefix = if (addServiceName) "$service/" else ""

    fun templateJoin(vararg parts: String) =
        templatePrefix + parts.joinToString("/")

    fun exampleJoin(vararg parts: String) =
        examplePrefix + parts.joinToString("/")

    val normalizedTitle = normalizeForFolder(postTitle)
    val titleId = "${normalizedTitle}_${postId}"

    return mapOf(
        DownloadFolderMode.CREATOR to PathExample(
            template = templateJoin("<creator>") + "/",
            example = exampleJoin(creator) + "/"
        ),
        DownloadFolderMode.CREATOR_POST_ID to PathExample(
            template = templateJoin("<creator>", "<postId>") + "/",
            example = exampleJoin(creator, postId) + "/"
        ),
        DownloadFolderMode.CREATOR_POST_TITLE_ID to PathExample(
            template = templateJoin("<creator>", "<postTitle>_<postId>") + "/",
            example = exampleJoin(creator, titleId) + "/"
        ),
        DownloadFolderMode.POST_ID to PathExample(
            template = templateJoin("<postId>") + "/",
            example = exampleJoin(postId) + "/"
        ),
        DownloadFolderMode.POST_TITLE_ID to PathExample(
            template = templateJoin("<postTitle>_<postId>") + "/",
            example = exampleJoin(titleId) + "/"
        ),
    )
}
