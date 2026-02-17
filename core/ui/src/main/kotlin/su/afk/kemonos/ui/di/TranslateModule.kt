package su.afk.kemonos.ui.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.ui.translate.MlKitTextTranslator
import su.afk.kemonos.ui.translate.TextTranslator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TranslateModule {

    @Provides
    @Singleton
    fun provideTextTranslator(): TextTranslator = MlKitTextTranslator()
}