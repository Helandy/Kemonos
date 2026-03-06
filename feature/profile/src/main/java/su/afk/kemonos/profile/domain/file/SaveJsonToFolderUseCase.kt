package su.afk.kemonos.profile.domain.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class SaveJsonToFolderUseCase @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) {
    /** Creates JSON file inside SAF folder and writes UTF-8 content to it. */
    operator fun invoke(
        folderUri: Uri,
        fileName: String,
        json: String,
    ) {
        val resolver = appContext.contentResolver
        val rwFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        runCatching { resolver.takePersistableUriPermission(folderUri, rwFlags) }

        val treeId = DocumentsContract.getTreeDocumentId(folderUri)
        val treeDocumentUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, treeId)

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
