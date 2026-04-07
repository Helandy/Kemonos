package su.afk.kemonos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import su.afk.kemonos.deepLink.handler.DeepLinkHandler
import su.afk.kemonos.main.api.IMainRoutingGraph
import su.afk.kemonos.main.api.IMainSettingsSync
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mainRoutingGraph: IMainRoutingGraph

    @Inject
    lateinit var mainSettingsSync: IMainSettingsSync

    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        deepLinkHandler.handle(intent)
        mainSettingsSync.start()

        setContent {
            mainRoutingGraph.MainGraph()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkHandler.handle(intent)
    }
}
