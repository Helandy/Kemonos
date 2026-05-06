@file:Suppress("DEPRECATION")

package su.afk.kemonos.auth.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthEncryptedPrefs

@Module
@InstallIn(SingletonComponent::class)
internal object AuthStorageModule {

    @Provides
    @Singleton
    fun provideMasterKey(@ApplicationContext context: Context): MasterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    @Provides
    @Singleton
    @AuthEncryptedPrefs
    fun provideAuthEncryptedPrefs(
        @ApplicationContext context: Context,
        masterKey: MasterKey,
    ): SharedPreferences =
        try {
            createEncryptedPrefs(context, masterKey)
        } catch (e: Exception) {
            context.deleteSharedPreferences("auth_secure_prefs")
            createEncryptedPrefs(context, masterKey)
        }

    /** На случай если будет перенос бэкапом с устройства на устройства и файл не расшифруется */
    private fun createEncryptedPrefs(context: Context, masterKey: MasterKey): SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "auth_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
}
