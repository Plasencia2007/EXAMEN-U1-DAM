package pe.upeu.biblioandes.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors: ColorScheme = lightColorScheme(
    primary = BALibroPrimaryLight,
    onPrimary = BALibroOnPrimaryLight,
    primaryContainer = BALibroPrimaryContainerLight,
    onPrimaryContainer = BALibroOnPrimaryContainerLight,
    secondary = BALibroSecondaryLight,
    onSecondary = BALibroOnSecondaryLight,
    secondaryContainer = BALibroSecondaryContainerLight,
    onSecondaryContainer = BALibroOnSecondaryContainerLight,
    tertiary = BALibroTertiaryLight,
    onTertiary = BALibroOnTertiaryLight,
    tertiaryContainer = BALibroTertiaryContainerLight,
    onTertiaryContainer = BALibroOnTertiaryContainerLight,
    error = BALibroErrorLight,
    onError = BALibroOnErrorLight,
    errorContainer = BALibroErrorContainerLight,
    onErrorContainer = BALibroOnErrorContainerLight,
    background = BALibroBackgroundLight,
    onBackground = BALibroOnBackgroundLight,
    surface = BALibroSurfaceLight,
    onSurface = BALibroOnSurfaceLight,
    surfaceVariant = BALibroSurfaceVariantLight,
    onSurfaceVariant = BALibroOnSurfaceVariantLight,
    outline = BALibroOutlineLight,
    outlineVariant = BALibroOutlineVariantLight,
    scrim = BALibroScrimLight,
    inverseSurface = BALibroInverseSurfaceLight,
    inverseOnSurface = BALibroInverseOnSurfaceLight,
    inversePrimary = BALibroInversePrimaryLight,
    surfaceDim = BALibroSurfaceDimLight,
    surfaceBright = BALibroSurfaceBrightLight,
    surfaceContainerLowest = BALibroSurfaceContainerLowestLight,
    surfaceContainerLow = BALibroSurfaceContainerLowLight,
    surfaceContainer = BALibroSurfaceContainerLight,
    surfaceContainerHigh = BALibroSurfaceContainerHighLight,
    surfaceContainerHighest = BALibroSurfaceContainerHighestLight
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = BALibroPrimaryDark,
    onPrimary = BALibroOnPrimaryDark,
    primaryContainer = BALibroPrimaryContainerDark,
    onPrimaryContainer = BALibroOnPrimaryContainerDark,
    secondary = BALibroSecondaryDark,
    onSecondary = BALibroOnSecondaryDark,
    secondaryContainer = BALibroSecondaryContainerDark,
    onSecondaryContainer = BALibroOnSecondaryContainerDark,
    tertiary = BALibroTertiaryDark,
    onTertiary = BALibroOnTertiaryDark,
    tertiaryContainer = BALibroTertiaryContainerDark,
    onTertiaryContainer = BALibroOnTertiaryContainerDark,
    error = BALibroErrorDark,
    onError = BALibroOnErrorDark,
    errorContainer = BALibroErrorContainerDark,
    onErrorContainer = BALibroOnErrorContainerDark,
    background = BALibroBackgroundDark,
    onBackground = BALibroOnBackgroundDark,
    surface = BALibroSurfaceDark,
    onSurface = BALibroOnSurfaceDark,
    surfaceVariant = BALibroSurfaceVariantDark,
    onSurfaceVariant = BALibroOnSurfaceVariantDark,
    outline = BALibroOutlineDark,
    outlineVariant = BALibroOutlineVariantDark,
    scrim = BALibroScrimDark,
    inverseSurface = BALibroInverseSurfaceDark,
    inverseOnSurface = BALibroInverseOnSurfaceDark,
    inversePrimary = BALibroInversePrimaryDark,
    surfaceDim = BALibroSurfaceDimDark,
    surfaceBright = BALibroSurfaceBrightDark,
    surfaceContainerLowest = BALibroSurfaceContainerLowestDark,
    surfaceContainerLow = BALibroSurfaceContainerLowDark,
    surfaceContainer = BALibroSurfaceContainerDark,
    surfaceContainerHigh = BALibroSurfaceContainerHighDark,
    surfaceContainerHighest = BALibroSurfaceContainerHighestDark
)

private val BiblioAndesShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun BiblioAndesTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = BiblioAndesShapes,
        typography = BiblioAndesTypography,
        content = content
    )
}
