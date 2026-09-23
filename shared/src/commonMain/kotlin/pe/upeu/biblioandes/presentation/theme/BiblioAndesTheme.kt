package pe.upeu.biblioandes.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

private fun colorSchemeDe(c: BiblioAndesColors, dark: Boolean): ColorScheme {
    return if (dark) {
        darkColorScheme(
            primary = c.primary, onPrimary = c.onPrimary,
            primaryContainer = c.pSoft, onPrimaryContainer = c.pText,
            secondary = c.accent, onSecondary = c.onPrimary,
            secondaryContainer = c.pSoft, onSecondaryContainer = c.pText,
            tertiary = c.ok, onTertiary = c.surface,
            tertiaryContainer = c.okBg, onTertiaryContainer = c.ok,
            error = c.bad, onError = c.onBad,
            errorContainer = c.badBg, onErrorContainer = c.bad,
            background = c.bg, onBackground = c.ink,
            surface = c.surface, onSurface = c.ink,
            surfaceVariant = c.track, onSurfaceVariant = c.ink2,
            outline = c.line, outlineVariant = c.line2
        )
    } else {
        lightColorScheme(
            primary = c.primary, onPrimary = c.onPrimary,
            primaryContainer = c.pSoft, onPrimaryContainer = c.pText,
            secondary = c.accent, onSecondary = c.onPrimary,
            secondaryContainer = c.pSoft, onSecondaryContainer = c.pText,
            tertiary = c.ok, onTertiary = c.surface,
            tertiaryContainer = c.okBg, onTertiaryContainer = c.ok,
            error = c.bad, onError = c.onBad,
            errorContainer = c.badBg, onErrorContainer = c.bad,
            background = c.bg, onBackground = c.ink,
            surface = c.surface, onSurface = c.ink,
            surfaceVariant = c.track, onSurfaceVariant = c.ink2,
            outline = c.line, outlineVariant = c.line2
        )
    }
}

private val BiblioAndesShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BiblioAndesTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colores = if (darkTheme) DarkBiblioAndesColors else LightBiblioAndesColors
    CompositionLocalProvider(LocalBiblioAndesColors provides colores) {
        MaterialTheme(
            colorScheme = colorSchemeDe(colores, darkTheme),
            shapes = BiblioAndesShapes,
            typography = BiblioAndesTypography,
            content = content
        )
    }
}
