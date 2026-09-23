package pe.upeu.biblioandes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.KoinContext
import pe.upeu.biblioandes.presentation.navigation.AppNavHost
import pe.upeu.biblioandes.presentation.navigation.DESTINOS
import pe.upeu.biblioandes.presentation.navigation.rememberBackStack
import pe.upeu.biblioandes.presentation.theme.BiblioAndesTheme
import pe.upeu.biblioandes.presentation.theme.LocalBiblioAndesColors

@Composable
@Preview
fun App() = KoinContext {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val backStack = rememberBackStack()

    BiblioAndesTheme(darkTheme = darkTheme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BarraNavegacionInferior(
                    destinoActual = backStack.actual,
                    onDestinoSeleccionado = { backStack.irARaiz(it) }
                )
            }
        ) { padding ->
            AppNavHost(
                backStack = backStack,
                modifier = Modifier.padding(padding),
                temaOscuro = darkTheme,
                onCambiarTema = { darkTheme = it }
            )
        }
    }
}
@Composable
private fun BarraNavegacionInferior(
    destinoActual: pe.upeu.biblioandes.presentation.navigation.Screen,
    onDestinoSeleccionado: (pe.upeu.biblioandes.presentation.navigation.Screen) -> Unit
) {
    val colores = LocalBiblioAndesColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colores.nav)
            .padding(top = 10.dp, bottom = 22.dp, start = 12.dp, end = 12.dp)
            .height(56.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
    ) {
        DESTINOS.forEach { destino ->
            val seleccionado = destinoActual == destino.screen
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickableSinIndicacion { onDestinoSeleccionado(destino.screen) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(32.dp)
                        .background(
                            if (seleccionado) colores.pSoft else androidx.compose.ui.graphics.Color.Transparent,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = destino.icono,
                        contentDescription = null,
                        tint = if (seleccionado) colores.pText else colores.ink2
                    )
                }
                Text(
                    text = destino.screen.titulo,
                    color = if (seleccionado) colores.pText else colores.ink2,
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun Modifier.clickableSinIndicacion(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)
