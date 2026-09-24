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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.usecase.LIMITE_PRESTAMOS_ACTIVOS
import pe.upeu.biblioandes.presentation.navigation.AppNavHost
import pe.upeu.biblioandes.presentation.navigation.BarraNavegacionViewModel
import pe.upeu.biblioandes.presentation.navigation.DESTINOS
import pe.upeu.biblioandes.presentation.navigation.Screen
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
    destinoActual: Screen,
    onDestinoSeleccionado: (Screen) -> Unit,
    // SC-B: el conteo de préstamos activos se lee del dominio (ObtenerPrestamosUseCase
    // vía BarraNavegacionViewModel), nunca se recalcula ni se hardcodea aquí.
    viewModel: BarraNavegacionViewModel = koinViewModel()
) {
    val colores = LocalBiblioAndesColors.current
    LaunchedEffect(destinoActual) { viewModel.recargar() }
    val prestamosActivos = viewModel.prestamosActivosCount
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
                    if (destino.screen is Screen.Prestamos && prestamosActivos > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .background(
                                    if (prestamosActivos >= LIMITE_PRESTAMOS_ACTIVOS) colores.bad else colores.accent,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$prestamosActivos",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }
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
