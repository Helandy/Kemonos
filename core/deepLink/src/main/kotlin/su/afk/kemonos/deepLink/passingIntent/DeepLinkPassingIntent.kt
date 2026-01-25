package su.afk.kemonos.deepLink.passingIntent

import android.content.Intent

interface DeepLinkPassingIntent {
    /**
     * @return true — интент обработан и дальше идти НЕ надо
     *         false — передать дальше в обычный deep link flow
     */
    fun intercept(intent: Intent): Boolean
}