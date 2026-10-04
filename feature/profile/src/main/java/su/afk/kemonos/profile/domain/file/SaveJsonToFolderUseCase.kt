package su.afk.kemonos.profile.domain.file

import javax.inject.Inject

internal class SaveJsonToFolderUseCase @Inject constructor(
    private val jsonFileStorage: IJsonFileStorage,
) {
    suspend operator fun invoke(
        folderUri: String,
        fileName: String,
        json: String,
    ) = jsonFileStorage.write(folderUri = folderUri, fileName = fileName, json = json)
}
