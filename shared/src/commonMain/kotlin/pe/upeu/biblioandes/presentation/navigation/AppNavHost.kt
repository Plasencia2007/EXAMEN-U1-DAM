package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pe.upeu.biblioandes.presentation.catalogo.CatalogoScreen
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroScreen
import pe.upeu.biblioandes.presentation.inicio.InicioScreen
import pe.upeu.biblioandes.presentation.prestamos.PrestamosScreen

/**
 * Pila de navegación propia: una lista de [Screen] donde el último elemento
 * es la pantalla visible. Empujar apila, retroceder desapila. Así se
 * resuelve "retorno correcto con el botón atrás del sistema" (RF-07) sin
 * depender de que cada pantalla sepa nada de la anterior.
 */
class BackStack {
    var pila by mutableStateOf(listOf<Screen>(Screen.Inicio))
        private set

    val actual: Screen get() = pila.last()
    val puedeRetroceder: Boolean get() = pila.size > 1

    fun navegarA(screen: Screen) {
        pila = pila + screen
    }

    fun retroceder() {
        if (puedeRetroceder) pila = pila.dropLast(1)
    }

    /** Cambia de destino raíz (usado por la barra inferior): limpia el resto de la pila. */
    fun irARaiz(screen: Screen) {
        pila = listOf(screen)
    }
}

@Composable
fun rememberBackStack(): BackStack = remember { BackStack() }

@Composable
fun AppNavHost(backStack: BackStack, modifier: Modifier = Modifier) {
    BackHandler(enabled = backStack.puedeRetroceder) { backStack.retroceder() }

    AnimatedContent(
        targetState = backStack.actual,
        modifier = modifier,
        label = "navegacion",
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { pantalla ->
        when (pantalla) {
            is Screen.Inicio -> InicioScreen(
                modifier = Modifier.fillMaxSize(),
                onIrACatalogo = { backStack.irARaiz(Screen.Catalogo) },
                onIrAPrestamos = { backStack.irARaiz(Screen.Prestamos) }
            )
            is Screen.Catalogo -> CatalogoScreen(
                modifier = Modifier.fillMaxSize(),
                onLibroSeleccionado = { libro -> backStack.navegarA(Screen.DetalleLibro(libro.id)) }
            )
            is Screen.Prestamos -> PrestamosScreen(modifier = Modifier.fillMaxSize())
            is Screen.Perfil -> PantallaPendiente("Perfil")
            is Screen.DetalleLibro -> DetalleLibroScreen(
                libroId = pantalla.libroId,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PantallaPendiente(nombre: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla $nombre (en construcción)")
    }
}
