package su.afk.kemonos.ui.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import su.afk.kemonos.constants.Constant.COIL_DISK_DIR_NAME
import su.afk.kemonos.preferences.ui.UiSettingKey.COIL_CACHE_SIZE_MB
import su.afk.kemonos.ui.imageLoader.imageProgress.ImageProgressStore
import su.afk.kemonos.ui.imageLoader.imageProgress.ProgressInterceptor
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoilOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageViewCoil

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    @CoilOkHttp
    fun provideCoilOkHttpClient(
        progressStore: ImageProgressStore,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(ProgressInterceptor(progressStore))
            .build()

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext appContext: Context,
        dataStore: DataStore<Preferences>,
        @CoilOkHttp okHttpClient: OkHttpClient,
    ): ImageLoader {
        val cacheMb = runBlocking(Dispatchers.IO) {
            dataStore.data.first()[COIL_CACHE_SIZE_MB] ?: DEFAULT_COIL_CACHE_MB
        }.coerceIn(MIN_COIL_CACHE_MB, MAX_COIL_CACHE_MB)

        val cacheBytes = cacheMb.toLong() * 1024L * 1024L

        return ImageLoader.Builder(appContext)
            .components {
                add(GifDecoder.Factory())
                add(OkHttpNetworkFetcherFactory(okHttpClient))
            }
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

    @Provides
    @Singleton
    @ImageViewCoil
    fun provideImageViewImageLoader(
        @ApplicationContext appContext: Context,
        @CoilOkHttp okHttpClient: OkHttpClient,
    ): ImageLoader = ImageLoader.Builder(appContext)
        .components {
            add(GifDecoder.Factory())
            add(OkHttpNetworkFetcherFactory(okHttpClient))
        }
        .crossfade(true)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizeBytes(IMAGE_VIEW_MIN_MEMORY_CACHE_BYTES)
                .build()
        }
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCache {
            DiskCache.Builder()
                .directory(appContext.cacheDir.resolve(IMAGE_VIEW_COIL_DISK_DIR_NAME))
                .maxSizeBytes(IMAGE_VIEW_DISK_CACHE_BYTES)
                .build()
        }
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()


    private const val DEFAULT_COIL_CACHE_MB = 300
    private const val MIN_COIL_CACHE_MB = 50
    private const val MAX_COIL_CACHE_MB = 500

    /** 150 mb RAM */
    private const val IMAGE_VIEW_MIN_MEMORY_CACHE_BYTES = 150L * 1024L * 1024L

    /** 100 mb DISK */
    private const val IMAGE_VIEW_DISK_CACHE_BYTES = 100L * 1024L * 1024L
    private const val IMAGE_VIEW_COIL_DISK_DIR_NAME = "${COIL_DISK_DIR_NAME}_image_view"
}
