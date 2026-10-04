package su.afk.kemonos.app.update.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import su.afk.kemonos.app.update.domain.repository.IAppVersionProvider
import su.afk.kemonos.app.update.util.currentVersionName
import javax.inject.Inject

internal class AppVersionProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : IAppVersionProvider {

    override fun currentVersionName(): String = context.currentVersionName()
}
