package su.afk.kemonos.setting.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.profile.presenter.setting.SettingNavigatorRegister
import su.afk.kemonos.setting.api.useCase.IGetSettingDestinationUseCase
import su.afk.kemonos.setting.navigation.GetSettingDestinationUseCase

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @IntoSet
    fun bindSettingNavigatorRegister(impl: SettingNavigatorRegister): NavRegistrar

    @Binds
    fun bindGetSettingDestinationUseCase(impl: GetSettingDestinationUseCase): IGetSettingDestinationUseCase
}
