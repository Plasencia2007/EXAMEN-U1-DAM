package pe.upeu.biblioandes.presentation.prestamos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo

@Composable
fun PrestamosScreen(
    modifier: Modifier = Modifier,
    viewModel: PrestamosViewModel = koinViewModel()
) {
    when (val estado = viewModel.uiState) {
        is PrestamosUiState.Cargando -> EstadoCargando(modifier)
        is PrestamosUiState.Contenido -> PrestamosContenido(
            modifier = modifier,
            estado = estado,
            onFiltroSeleccionado = viewModel::seleccionarFiltro
        )
    }
}

@Composable
private fun PrestamosContenido(
    modifier: Modifier,
    estado: PrestamosUiState.Contenido,
    onFiltroSeleccionado: (FiltroEstado) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FiltroEstado.entries) { filtro ->
                FilterChip(
                    selected = estado.filtroSeleccionado == filtro,
                    onClick = { onFiltroSeleccionado(filtro) },
                    label = { Text(filtro.etiqueta) }
                )
            }
        }

        if (estado.estaVacio) {
            EstadoVacio(Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.prestamos, key = { it.id }) { prestamo ->
                    TarjetaPrestamo(prestamo)
                }
            }
        }
    }
}

@Composable
private fun TarjetaPrestamo(prestamo: Prestamo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(prestamo.libro.titulo, style = MaterialTheme.typography.titleMedium)
            Text(prestamo.libro.autor, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Prestado: ${prestamo.fechaPrestamo}  ·  Límite: ${prestamo.fechaLimite}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = descripcionEstado(prestamo.estado),
                style = MaterialTheme.typography.labelLarge,
                color = colorEstado(prestamo.estado),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun descripcionEstado(estado: EstadoPrestamo): String = when (estado) {
    is EstadoPrestamo.Activo -> "Activo · ${estado.diasRestantes} días restantes"
    is EstadoPrestamo.Devuelto -> "Devuelto el ${estado.fechaDevolucion}"
    is EstadoPrestamo.Vencido -> "Vencido · ${estado.diasDeAtraso} días de atraso"
}

@Composable
private fun colorEstado(estado: EstadoPrestamo) = when (estado) {
    is EstadoPrestamo.Activo -> MaterialTheme.colorScheme.primary
    is EstadoPrestamo.Devuelto -> MaterialTheme.colorScheme.secondary
    is EstadoPrestamo.Vencido -> MaterialTheme.colorScheme.error
}

@Composable
private fun EstadoCargando(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EstadoVacio(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AutoStories, contentDescription = null)
            Text("No tienes préstamos con ese filtro")
        }
    }
}
