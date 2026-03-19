package su.afk.kemonos.deepLink.passingIntent

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeepLinkPassingIntentImpl @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) : DeepLinkPassingIntent {

    override fun intercept(intent: Intent): Boolean {
        intent.data ?: return false


        return false
    }
}
