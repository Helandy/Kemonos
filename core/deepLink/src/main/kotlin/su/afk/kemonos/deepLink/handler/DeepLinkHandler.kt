package su.afk.kemonos.deepLink.handler

import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import su.afk.kemonos.deepLink.resolver.DeepLinkResolver
import su.afk.kemonos.navigation.NavigationManager
import javax.inject.Inject
import javax.inject.Named

interface DeepLinkHandler {
    fun handle(intent: Intent)
}

internal class DeepLinkHandlerImpl @Inject constructor(
    private val resolver: DeepLinkResolver,
    private val navigationManager: NavigationManager,
    @Named("MainScope") private val mainScope: CoroutineScope,
) : DeepLinkHandler {

    override fun handle(intent: Intent) {
        val uri = intent.data ?: return

        mainScope.launch {
            resolver.resolve(uri)?.let(navigationManager::navigate)
        }
    }
}