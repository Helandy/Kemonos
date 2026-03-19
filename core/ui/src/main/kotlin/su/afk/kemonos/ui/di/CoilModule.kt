package su.afk.kemonos.ui.di

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
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
import su.afk.kemonos.ui.imageLoader.CoilRequestLoggingInterceptor
import su.afk.kemonos.ui.imageLoader.SuccessOnlyImageCacheStrategy
import su.afk.kemonos.ui.imageLoader.imageProgress.ImageProgressStore
import su.afk.kemonos.ui.imageLoader.imageProgress.ProgressInterceptor
import java.util.concurrent.TimeUnit
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
        @ApplicationContext appContext: Context,
        progressStore: ImageProgressStore,
    ): OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (appContext.isDebuggableApp()) {
                addInterceptor(CoilRequestLoggingInterceptor())
            }
        }
            .addNetworkInterceptor(ProgressInterceptor(progressStore))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    /** Превью картинок */
    @Provides
    @Singleton
    @OptIn(ExperimentalCoilApi::class)
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
                // Avoid android.graphics.Movie-based GIF decoding, which can crash on cancellation.
                add(AnimatedImageDecoder.Factory())
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = { okHttpClient },
                        cacheStrategy = { SuccessOnlyImageCacheStrategy },
                    )
                )
            }
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(appContext, 0.25)
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

    /** Фулл просмотр картинки */
    @Provides
    @Singleton
    @ImageViewCoil
    @OptIn(ExperimentalCoilApi::class)
    fun provideImageViewImageLoader(
        @ApplicationContext appContext: Context,
        @CoilOkHttp okHttpClient: OkHttpClient,
    ): ImageLoader = ImageLoader.Builder(appContext)
        .components {
            // Keep the image viewer on the same decoder path as the app-wide loader.
            add(AnimatedImageDecoder.Factory())
            add(
                OkHttpNetworkFetcherFactory(
                    callFactory = { okHttpClient },
                    cacheStrategy = { SuccessOnlyImageCacheStrategy },
                )
            )
        }
        .crossfade(true)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(appContext, 0.20)
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


    private const val DEFAULT_COIL_CACHE_MB = 500
    private const val MIN_COIL_CACHE_MB = 100
    private const val MAX_COIL_CACHE_MB = 800

    /** 100 mb DISK */
    private const val IMAGE_VIEW_DISK_CACHE_BYTES = 100L * 1024L * 1024L
    private const val IMAGE_VIEW_COIL_DISK_DIR_NAME = "${COIL_DISK_DIR_NAME}_image_view"
}

private fun Context.isDebuggableApp(): Boolean =
    (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
