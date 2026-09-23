package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.runtime.Composable

/**
 * Intercepta el botón/gesto atrás del sistema (RF-07). Compose Multiplatform
 * 1.12.1 no expone todavía un `BackHandler` común, así que cada plataforma
 * aporta su propia implementación: real en Android, no-op en iOS (donde el
 * retorno se resuelve con el gesto de swipe nativo, no un botón físico).
 */
@Composable
expect fun BackHandler(enabled: Boolean, onBack: () -> Unit)
