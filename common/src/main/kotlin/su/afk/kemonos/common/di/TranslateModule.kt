package su.afk.kemonos.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.common.translate.IOpenTranslateUseCase
import su.afk.kemonos.common.translate.OpenTranslateUseCase

@Module
@InstallIn(SingletonComponent::class)
interface TranslateModule {
    @Binds
    fun bindOpenTranslateUseCase(impl: OpenTranslateUseCase): IOpenTranslateUseCase
}