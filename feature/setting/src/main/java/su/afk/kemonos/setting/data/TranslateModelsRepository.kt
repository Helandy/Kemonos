package su.afk.kemonos.setting.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.afk.kemonos.setting.domain.model.TranslateModelInfo
import su.afk.kemonos.setting.domain.repository.ITranslateModelsRepository
import java.io.File
import javax.inject.Inject

class TranslateModelsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : ITranslateModelsRepository {

    override suspend fun getDownloadedModels(): List<TranslateModelInfo> = withContext(Dispatchers.IO) {
        val root = modelsRootDir()
        if (!root.exists() || !root.isDirectory) return@withContext emptyList()

        root.listFiles()
            .orEmpty()
            .asSequence()
            .filter { it.isDirectory }
            .filter { isModelDirectoryName(it.name) }
            .mapNotNull { dir ->
                val sourceAndTarget = parseLanguagePair(dir.name) ?: return@mapNotNull null
                TranslateModelInfo(
                    id = dir.name,
                    sourceLanguageTag = sourceAndTarget.first,
                    targetLanguageTag = sourceAndTarget.second,
                    sizeBytes = dir.directorySizeBytes(),
                )
            }
            .sortedWith(compareBy({ it.targetLanguageTag }, { it.sourceLanguageTag }))
            .toList()
    }

    override suspend fun deleteDownloadedModel(modelId: String): Boolean = withContext(Dispatchers.IO) {
        if (!isModelDirectoryName(modelId)) return@withContext false

        val dir = File(modelsRootDir(), modelId)
        if (!dir.exists()) return@withContext true
        if (!dir.isDirectory) return@withContext false

        dir.deleteRecursively() && !dir.exists()
    }

    private fun modelsRootDir(): File = File(context.noBackupFilesDir, MODEL_ROOT_DIR_NAME)

    private fun isModelDirectoryName(name: String): Boolean =
        name != TEMP_DIR_NAME && parseLanguagePair(name) != null

    private fun parseLanguagePair(name: String): Pair<String, String>? {
        val pair = name.split("_", limit = 2)
        if (pair.size != 2) return null
        val source = pair[0].trim()
        val target = pair[1].trim()
        if (source.isBlank() || target.isBlank()) return null
        return source to target
    }

    private fun File.directorySizeBytes(): Long =
        walkTopDown()
            .filter { it.isFile }
            .sumOf { it.length() }

    private companion object {
        private const val MODEL_ROOT_DIR_NAME = "com.google.mlkit.translate.models"
        private const val TEMP_DIR_NAME = "temp"
    }
}
