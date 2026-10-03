package su.afk.kemonos.profile.domain.file

import javax.inject.Inject

internal class ReadJsonFromUriUseCase @Inject constructor(
    private val jsonFileStorage: IJsonFileStorage,
) {
    operator fun invoke(fileUri: String): String = jsonFileStorage.read(fileUri)
}
