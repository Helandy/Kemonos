package su.afk.kemonos.ui.uiUtils.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.security.MessageDigest

fun resolveDisplayName(context: Context, uri: Uri): String? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            cursor.getString(nameIndex)
        } else {
            null
        }
    }
}

fun sha256FromUri(context: Context, uri: Uri): String? {
    return context.contentResolver.openInputStream(uri)?.use { input ->
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        while (true) {
            val read = input.read(buffer)
            if (read == -1) break
            digest.update(buffer, 0, read)
        }

        digest.digest().joinToString(separator = "") { byte ->
            "%02x".format(byte)
        }
    }
}
