package su.afk.kemonos.setting.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.setting.data.TranslateModelsRepository
import su.afk.kemonos.setting.domain.repository.ITranslateModelsRepository

@Module
@InstallIn(SingletonComponent::class)
interface TranslateModelsModule {

    @Binds
    fun bindTranslateModelsRepository(impl: TranslateModelsRepository): ITranslateModelsRepository
}
