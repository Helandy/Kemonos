package su.afk.kemonos.common.di

import android.content.Context
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext appContext: Context,
    ): ImageLoader {
        return ImageLoader.Builder(appContext)
            .components {
                add(GifDecoder.Factory())
            }
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    // 20% от доступной памяти приложения под картинки
                    .maxSizePercent(appContext, 0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(appContext.cacheDir.resolve("image_cache"))
                    // 256 mb cache disk
                    .maxSizeBytes(256L * 1024L * 1024L)
                    .build()
            }
            .build()
    }
}