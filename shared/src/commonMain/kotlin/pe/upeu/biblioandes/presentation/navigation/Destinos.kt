package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

/** Rutas tipadas de la aplicación (RF-07). */
sealed class Screen(val titulo: String) {
    data object Inicio : Screen("Inicio")
    data object Catalogo : Screen("Catálogo")
    data object Prestamos : Screen("Mis préstamos")
    data object Perfil : Screen("Perfil")
    data class DetalleLibro(val libroId: Int) : Screen("Detalle del libro")
}

data class Destino(val screen: Screen, val icono: ImageVector)

/** Única fuente de verdad para la barra de navegación inferior. */
val DESTINOS = listOf(
    Destino(Screen.Inicio, Icons.Default.Home),
    Destino(Screen.Catalogo, Icons.Default.MenuBook),
    Destino(Screen.Prestamos, Icons.Default.AutoStories)
)
