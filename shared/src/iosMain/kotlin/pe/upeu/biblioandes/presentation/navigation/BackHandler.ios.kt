package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS no tiene un botón de sistema equivalente: el retorno se resuelve
    // con el swipe nativo del contenedor UIKit, no requiere intercepción aquí.
}
