package su.afk.kemonos.presenter.bottomBar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import su.afk.kemonos.navigation.tab.BottomTab

private val BottomBarHeight: Dp = 48.dp
private val BottomBarIconSize: Dp = 20.dp

@Composable
internal fun BottomNavigationBar(
    currentTab: BottomTab,
    onTabClick: (BottomTab) -> Unit
) {
    val items = listOf(
        BottomTab.CREATORS to Icons.Filled.Groups,
        BottomTab.POSTS to Icons.AutoMirrored.Filled.Article,
        BottomTab.PROFILE to Icons.Filled.AccountCircle,
    )

    NavigationBar(
        modifier = Modifier.height(BottomBarHeight),
        windowInsets = WindowInsets(0)
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
                        modifier = Modifier.size(BottomBarIconSize)
                    )
                },
            )
        }
    }
}
