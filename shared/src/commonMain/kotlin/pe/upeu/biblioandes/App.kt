package pe.upeu.biblioandes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import pe.upeu.biblioandes.presentation.navigation.AppNavHost
import pe.upeu.biblioandes.presentation.navigation.DESTINOS
import pe.upeu.biblioandes.presentation.navigation.rememberBackStack
import pe.upeu.biblioandes.presentation.theme.BiblioAndesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() = KoinContext {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val backStack = rememberBackStack()

    BiblioAndesTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(backStack.actual.titulo) },
                    actions = {
                        IconButton(onClick = { darkTheme = !darkTheme }) {
                            Icon(
                                imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Cambiar tema"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    DESTINOS.forEach { destino ->
                        val destinoRaiz = destino.screen
                        NavigationBarItem(
                            selected = backStack.actual == destinoRaiz,
                            onClick = { backStack.irARaiz(destinoRaiz) },
                            icon = { Icon(destino.icono, contentDescription = null) },
                            label = { Text(destino.screen.titulo) }
                        )
                    }
                }
            }
        ) { padding ->
            AppNavHost(backStack = backStack, modifier = Modifier.padding(padding))
        }
    }
}
