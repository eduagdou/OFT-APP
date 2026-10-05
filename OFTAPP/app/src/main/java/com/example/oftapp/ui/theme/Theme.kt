package com.example.oftapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    onPrimaryContainer = Color(0xFF002244),

    secondary = VerdeSecundario,
    onSecondary = Color.White,
    secondaryContainer = VerdeContenedor,
    onSecondaryContainer = Color(0xFF003832),

    tertiary = VerdeSecundario,
    onTertiary = Color.White,

    background = FondoApp,
    onBackground = TextoPrincipal,

    surface = SuperficieBlanca,
    onSurface = TextoPrincipal,
    surfaceVariant = AzulContenedor,
    onSurfaceVariant = TextoSecundario,

    outline = BordeSuave,
    outlineVariant = Color(0xFFD0D7DE)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4D9BFF),
    onPrimary = Color(0xFF003266),
    primaryContainer = Color(0xFF004999),
    onPrimaryContainer = Color(0xFFDCE3FF),

    secondary = Color(0xFF33C7B5),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFF9CF0E4),

    background = Color(0xFF121417),
    onBackground = Color(0xFFE1E2E5),

    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE1E2E5),
    surfaceVariant = Color(0xFF282F36),
    onSurfaceVariant = Color(0xFFC2C7CE),

    outline = Color(0xFF41474D)
)

@Composable
fun OFTAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivamos dynamicColor por defecto para mantener la paleta institucional (#0066CC, #00A896, #F8F9FA, #1A1C1E)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
