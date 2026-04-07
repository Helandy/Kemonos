package su.afk.kemonos.preferences.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Files

class UiSettingUseCaseVideoPreviewAspectRatioTest {

    @Test
    fun unknownAspectRatioFallsBackToDefault() = runBlocking {
        val dataStore = createDataStore()
        dataStore.edit { prefs ->
            prefs[UiSettingKey.VIDEO_PREVIEW_ASPECT_RATIO] = "RATIO_2_1"
        }

        val useCase = UiSettingUseCase(dataStore)
        val actual = useCase.prefs.first().videoPreviewAspectRatio

        assertEquals(VideoPreviewAspectRatio.RATIO_1_1, actual)
    }

    @Test
    fun knownAspectRatiosAreReadCorrectly() = runBlocking {
        val cases = listOf(
            "RATIO_16_9" to VideoPreviewAspectRatio.RATIO_16_9,
            "RATIO_4_3" to VideoPreviewAspectRatio.RATIO_4_3,
            "RATIO_1_1" to VideoPreviewAspectRatio.RATIO_1_1,
            "RATIO_3_4" to VideoPreviewAspectRatio.RATIO_3_4,
            "RATIO_9_16" to VideoPreviewAspectRatio.RATIO_9_16,
        )

        cases.forEach { (rawValue, expected) ->
            val dataStore = createDataStore()
            dataStore.edit { prefs ->
                prefs[UiSettingKey.VIDEO_PREVIEW_ASPECT_RATIO] = rawValue
            }

            val useCase = UiSettingUseCase(dataStore)
            val actual = useCase.prefs.first().videoPreviewAspectRatio

            assertEquals(expected, actual)
        }
    }

    private fun createDataStore(): DataStore<Preferences> {
        val file = Files.createTempFile("ui-setting-test-", ".preferences_pb").toFile()
        file.deleteOnExit()
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { file },
        )
    }
}
