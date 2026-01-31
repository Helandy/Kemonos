package su.afk.kemonos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import su.afk.kemonos.common.di.LocalDomainResolver
import su.afk.kemonos.common.error.ErrorMapper
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.LocalErrorMapper
import su.afk.kemonos.common.imageLoader.LocalAppImageLoader
import su.afk.kemonos.common.video.LocalVideoFrameCache
import su.afk.kemonos.navigation.AppNavHost
import su.afk.kemonos.navigation.NavRegistrar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.IDomainResolver
import su.afk.kemonos.presenter.bottomBar.BottomNavigationBar
import su.afk.kemonos.storage.api.videoPreview.IVideoFrameCache
import su.afk.kemonos.ui.theme.KemonosTheme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRoutingGraph @Inject constructor(
    private val domainResolver: IDomainResolver,
    private val imageLoader: ImageLoader,
    private val videoFrameCache: IVideoFrameCache,
    private val errorHandler: IErrorHandlerUseCase,
    private val navManager: NavigationManager,
    private val registrars: Set<@JvmSuppressWildcards NavRegistrar>
) {

    @Composable
    fun MainGraph() {
        KemonosTheme {
            Scaffold(
//                modifier = Modifier.fillMaxSize(),
//                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    val inTabs = navManager.startAppBackStack.isEmpty()
                    if (inTabs) {
                        BottomNavigationBar(
                            currentTab = navManager.currentTab,
                            onTabClick = { tab -> navManager.switchTab(tab) }
                        )
                    }
                }
            ) {
                CompositionLocalProvider(
                    LocalDomainResolver provides domainResolver,
                    LocalAppImageLoader provides imageLoader,
                    LocalVideoFrameCache provides videoFrameCache,
                    LocalErrorMapper provides ErrorMapper { t -> errorHandler.parse(t) },
                ) {
                    AppNavHost(
                        navManager = navManager,
                        registrars = registrars,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
