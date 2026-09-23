package pe.upeu.biblioandes.presentation.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Paleta con nombres semánticos propios (no los roles genéricos de Material 3):
 * calca 1 a 1 las variables CSS del prototipo de diseño, para que cada
 * pantalla pinte exactamente los mismos tonos en claro y oscuro.
 */
data class BiblioAndesColors(
    val bg: Color,
    val surface: Color,
    val ink: Color,
    val ink2: Color,
    val ink3: Color,
    val line: Color,
    val line2: Color,
    val dashed: Color,
    val primary: Color,
    val onPrimary: Color,
    val pSoft: Color,
    val pText: Color,
    val accent: Color,
    val okBg: Color,
    val ok: Color,
    val badBg: Color,
    val bad: Color,
    val onBad: Color,
    val badLine: Color,
    val track: Color,
    val hero: Color,
    val chipOn: Color,
    val onChipOn: Color,
    val segOn: Color,
    val feature: Color,
    val onFeature: Color,
    val feature2: Color,
    val featTrack: Color,
    val featBar: Color,
    val chev: Color,
    val nav: Color,
    val shadow: Color
)

val LightBiblioAndesColors = BiblioAndesColors(
    bg = BgLight, surface = SurfaceLight, ink = InkLight, ink2 = Ink2Light, ink3 = Ink3Light,
    line = LineLight, line2 = Line2Light, dashed = DashedLight,
    primary = PrimaryLight, onPrimary = OnPrimaryLight, pSoft = PSoftLight, pText = PTextLight,
    accent = AccentLight, okBg = OkBgLight, ok = OkLight,
    badBg = BadBgLight, bad = BadLight, onBad = OnBadLight, badLine = BadLineLight,
    track = TrackLight, hero = HeroLight, chipOn = ChipOnLight, onChipOn = OnChipOnLight,
    segOn = SegOnLight, feature = FeatureLight, onFeature = OnFeatureLight, feature2 = Feature2Light,
    featTrack = FeatTrackLight, featBar = FeatBarLight, chev = ChevLight, nav = NavLight, shadow = ShadowLight
)

val DarkBiblioAndesColors = BiblioAndesColors(
    bg = BgDark, surface = SurfaceDark, ink = InkDark, ink2 = Ink2Dark, ink3 = Ink3Dark,
    line = LineDark, line2 = Line2Dark, dashed = DashedDark,
    primary = PrimaryDark, onPrimary = OnPrimaryDark, pSoft = PSoftDark, pText = PTextDark,
    accent = AccentDark, okBg = OkBgDark, ok = OkDark,
    badBg = BadBgDark, bad = BadDark, onBad = OnBadDark, badLine = BadLineDark,
    track = TrackDark, hero = HeroDark, chipOn = ChipOnDark, onChipOn = OnChipOnDark,
    segOn = SegOnDark, feature = FeatureDark, onFeature = OnFeatureDark, feature2 = Feature2Dark,
    featTrack = FeatTrackDark, featBar = FeatBarDark, chev = ChevDark, nav = NavDark, shadow = ShadowDark
)

val LocalBiblioAndesColors = compositionLocalOf { LightBiblioAndesColors }
