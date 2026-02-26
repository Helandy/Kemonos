package su.afk.kemonos.storage.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import su.afk.kemonos.storage.database.CommunityDatabase
import su.afk.kemonos.storage.entity.communityCache.dao.CommunityCacheDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseCommunityModule {

    @Provides
    @Singleton
    fun provideCommunityDatabase(@ApplicationContext context: Context): CommunityDatabase =
        Room.databaseBuilder(context, CommunityDatabase::class.java, "community_db").build()

    @Provides
    fun provideCommunityCacheDao(db: CommunityDatabase): CommunityCacheDao = db.communityCacheDao()
}
