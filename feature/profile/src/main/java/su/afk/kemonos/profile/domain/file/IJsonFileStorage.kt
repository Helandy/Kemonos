package su.afk.kemonos.profile.domain.file

internal interface IJsonFileStorage {
    /** Reads UTF-8 JSON text from the document identified by [fileUri]. */
    suspend fun read(fileUri: String): String

    /** Creates a JSON file named [fileName] inside the folder [folderUri] and writes UTF-8 [json] to it. */
    suspend fun write(folderUri: String, fileName: String, json: String)
}
