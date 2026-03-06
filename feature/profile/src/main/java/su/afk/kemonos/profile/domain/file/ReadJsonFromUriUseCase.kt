package su.afk.kemonos.profile.domain.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class ReadJsonFromUriUseCase @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) {
    /** Reads UTF-8 JSON text from SAF uri and tries to persist read permission. */
    operator fun invoke(fileUri: Uri): String {
        val resolver = appContext.contentResolver
        runCatching {
            resolver.takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return resolver.openInputStream(fileUri)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
            reader.readText()
        } ?: error("Failed to open import file stream")
    }
}
