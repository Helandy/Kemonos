package su.afk.kemonos.common.translate

import android.content.Context
import android.content.Intent
import javax.inject.Inject

interface IOpenTranslateUseCase {
    fun openGoogleTranslateApp(context: Context, text: String, targetLang: String? = null)
}

class OpenTranslateUseCase @Inject constructor() : IOpenTranslateUseCase {

    override fun openGoogleTranslateApp(context: Context, text: String, targetLang: String?) {
        val clean = text.trim()
        if (clean.isEmpty()) return

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, clean)
            setPackage("com.google.android.apps.translate")
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } else {
            context.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, clean)
                    },
                    "Перевести через…"
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}