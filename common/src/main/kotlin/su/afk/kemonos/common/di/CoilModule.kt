package su.afk.kemonos.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.request.crossfade
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import su.afk.kemonos.common.constants.Constant.COIL_DISK_DIR_NAME
import su.afk.kemonos.preferences.ui.UiSettingKey.COIL_CACHE_SIZE_MB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext appContext: Context,
        dataStore: DataStore<Preferences>,
    ): ImageLoader {
        val cacheMb = runBlocking(Dispatchers.IO) {
            val p = dataStore.data.first()
            p[COIL_CACHE_SIZE_MB] ?: DEFAULT_COIL_CACHE_MB
        }.coerceIn(MIN_COIL_CACHE_MB, MAX_COIL_CACHE_MB)

        val cacheBytes = cacheMb.toLong() * 1024L * 1024L

        return ImageLoader.Builder(appContext)
            .components { add(GifDecoder.Factory()) }
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(appContext, 0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(appContext.cacheDir.resolve(COIL_DISK_DIR_NAME))
                    .maxSizeBytes(cacheBytes)
                    .build()
            }
            .build()
    }

    private const val DEFAULT_COIL_CACHE_MB = 300
    private const val MIN_COIL_CACHE_MB = 50
    private const val MAX_COIL_CACHE_MB = 500
}