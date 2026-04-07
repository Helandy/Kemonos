package su.afk.kemonos.main.route.bottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import su.afk.kemonos.navigation.tab.BottomTab

private val BottomBarHeightGestures: Dp = 58.dp
private val BottomBarIconGestures: Dp = 24.dp

private val BottomBarHeightThreeButtons: Dp = 46.dp
private val BottomBarIconThreeButtons: Dp = 20.dp

@Composable
internal fun BottomNavigationBar(
    currentTab: BottomTab,
    onTabClick: (BottomTab) -> Unit,
) {
    val items = listOf(
        BottomTab.CREATORS to Icons.Filled.Groups,
        BottomTab.POSTS to Icons.AutoMirrored.Filled.Article,
        BottomTab.PROFILE to Icons.Filled.AccountCircle,
    )
    val density = LocalDensity.current
    val navBarBottomInsetPx = NavigationBarDefaults.windowInsets.getBottom(density)
    val hasThreeButtonNavigation = with(density) { navBarBottomInsetPx.toDp() >= 40.dp }

    val bottomBarHeight = if (hasThreeButtonNavigation) {
        BottomBarHeightThreeButtons
    } else {
        BottomBarHeightGestures
    }
    val bottomBarIconSize = if (hasThreeButtonNavigation) {
        BottomBarIconThreeButtons
    } else {
        BottomBarIconGestures
    }
    val systemBarLikeColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(systemBarLikeColor)
            .navigationBarsPadding(),
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomBarHeight),
            windowInsets = WindowInsets(0),
            containerColor = systemBarLikeColor,
        ) {
            items.forEach { (tab, icon) ->
                val selected = tab == currentTab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabClick(tab) },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(bottomBarIconSize),
                        )
                    },
                )
            }
        }
    }
}
