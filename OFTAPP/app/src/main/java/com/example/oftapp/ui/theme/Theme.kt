package com.example.oftapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

val LocalButtonScale = staticCompositionLocalOf { 1.0f }
val LocalHighContrast = staticCompositionLocalOf { false }

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

// Paleta de Alto Contraste para Baja Visión (WCAG 2.2 AAA)
private val HighContrastColorScheme = darkColorScheme(
    primary = Color(0xFFFFFF00), // Amarillo puro
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF000000),
    onPrimaryContainer = Color(0xFFFFFF00),

    secondary = Color(0xFF00FFFF), // Cian puro
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF000000),
    onSecondaryContainer = Color(0xFF00FFFF),

    background = Color.Black, // Negro absoluto #000000
    onBackground = Color.White,

    surface = Color(0xFF0A0A0A),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1F1F1F),
    onSurfaceVariant = Color(0xFFFFFF00),

    outline = Color.White
)

@Composable
fun OFTAPPTheme(
    modoTema: String = "SISTEMA", // "SISTEMA", "CLARO", "OSCURO"
    altoContraste: Boolean = false,
    escalaBotones: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (modoTema) {
        "CLARO" -> false
        "OSCURO" -> true
        else -> systemInDark
    }

    val colorScheme = when {
        altoContraste -> HighContrastColorScheme
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val currentDensity = LocalDensity.current
    val scaledDensity = Density(
        density = currentDensity.density * escalaBotones,
        fontScale = currentDensity.fontScale * escalaBotones
    )

    CompositionLocalProvider(
        LocalDensity provides scaledDensity,
        LocalButtonScale provides escalaBotones,
        LocalHighContrast provides altoContraste
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
