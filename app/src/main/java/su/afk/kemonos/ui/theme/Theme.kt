package su.afk.kemonos.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import su.afk.kemonos.ui.uiUtils.findActivity

private val DarkColorScheme = darkColorScheme(
)

private val LightColorScheme = lightColorScheme(
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun KemonosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    val context = LocalContext.current
    val barsColor = colorScheme.surface
    val darkIcons = barsColor.luminance() > 0.5f

    if (!view.isInEditMode) {
        SideEffect {
            val activity = context.findActivity() as? ComponentActivity ?: return@SideEffect
            val colorInt = barsColor.toArgb()
            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(colorInt, colorInt),
                navigationBarStyle = SystemBarStyle.auto(colorInt, colorInt),
            )
            val insetsController = WindowCompat.getInsetsController(activity.window, view)
            insetsController.isAppearanceLightStatusBars = darkIcons
            insetsController.isAppearanceLightNavigationBars = darkIcons
            activity.window.isNavigationBarContrastEnforced = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
