package su.afk.kemonos.profile.data.file

import android.content.Context
import android.content.Intent
import android.provider.DocumentsContract
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.kemonos.profile.domain.file.IJsonFileStorage
import javax.inject.Inject

/** SAF-реализация: читает и пишет JSON через [android.content.ContentResolver]. */
internal class JsonFileStorage @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) : IJsonFileStorage {

    override fun read(fileUri: String): String {
        val uri = Uri.parse(fileUri)
        val resolver = appContext.contentResolver
        runCatching {
            resolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return resolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
            reader.readText()
        } ?: error("Failed to open import file stream")
    }

    override fun write(folderUri: String, fileName: String, json: String) {
        val folder = Uri.parse(folderUri)
        val resolver = appContext.contentResolver
        val rwFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        runCatching { resolver.takePersistableUriPermission(folder, rwFlags) }

        val treeId = DocumentsContract.getTreeDocumentId(folder)
        val treeDocumentUri = DocumentsContract.buildDocumentUriUsingTree(folder, treeId)

        val createdFileUri = DocumentsContract.createDocument(
            resolver,
            treeDocumentUri,
            "application/json",
            fileName,
        ) ?: error("Failed to create export file")

        resolver.openOutputStream(createdFileUri)?.use { stream ->
            stream.write(json.toByteArray(Charsets.UTF_8))
        } ?: error("Failed to open export file stream")
    }
}
