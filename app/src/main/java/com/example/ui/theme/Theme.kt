package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SophisticatedColorScheme = darkColorScheme(
    primary = YaftaPrimaryGold,
    onPrimary = YaftaSecondaryCharcoal,
    secondary = YaftaSecondaryCharcoal,
    onSecondary = Color.White,
    tertiary = YaftaAccentAmber,
    background = YaftaDarkBg,
    onBackground = YaftaDarkText,
    surface = YaftaDarkSurface,
    onSurface = YaftaDarkText,
    surfaceVariant = YaftaDarkCard,
    onSurfaceVariant = YaftaDarkTextMuted,
    outline = YaftaDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = YaftaAccentAmber,
    onPrimary = YaftaSecondaryCharcoal,
    secondary = YaftaSecondaryCharcoal,
    tertiary = YaftaPrimaryGold,
    background = YaftaLightBg,
    onBackground = YaftaLightText,
    surface = YaftaLightSurface,
    onSurface = YaftaLightText,
    surfaceVariant = YaftaLightCard,
    onSurfaceVariant = YaftaLightTextMuted,
    outline = YaftaLightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to true to support Sophisticated Dark by default
    dynamicColor: Boolean = false, // Disable dynamic colors by default to retain custom design
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SophisticatedColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
