package su.afk.kemonos.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.common.translate.MlKitTextTranslator
import su.afk.kemonos.common.translate.TextTranslator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TranslateModule {

    @Provides
    @Singleton
    fun provideTextTranslator(): TextTranslator = MlKitTextTranslator()
}