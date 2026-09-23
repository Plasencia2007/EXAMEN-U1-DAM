package pe.upeu.biblioandes.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pe.upeu.biblioandes.domain.model.Libro

@Composable
fun DetalleLibroScreen(
    libroId: Int,
    modifier: Modifier = Modifier,
    viewModel: DetalleLibroViewModel = koinViewModel(key = "detalle-$libroId") { parametersOf(libroId) }
) {
    val estadoSolicitud = viewModel.estadoSolicitud

    when (val estado = viewModel.uiState) {
        is DetalleLibroUiState.Cargando -> EstadoCargando(modifier)
        is DetalleLibroUiState.NoEncontrado -> EstadoNoEncontrado(modifier)
        is DetalleLibroUiState.Contenido -> DetalleContenido(
            modifier = modifier,
            libro = estado.libro,
            estadoSolicitud = estadoSolicitud,
            onSolicitar = viewModel::pedirConfirmacion,
            onConfirmar = viewModel::confirmarSolicitud,
            onCancelar = viewModel::cancelarConfirmacion
        )
    }
}

@Composable
private fun DetalleContenido(
    modifier: Modifier,
    libro: Libro,
    estadoSolicitud: EstadoSolicitud,
    onSolicitar: () -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(libro.titulo, style = MaterialTheme.typography.headlineSmall)
        Text(libro.autor, style = MaterialTheme.typography.titleMedium)

        Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilaDato("Año", libro.anio.toString())
            FilaDato("Categoría", libro.categoria)
            FilaDato("Sede", libro.sede)
            FilaDato(
                etiqueta = "Ejemplares disponibles",
                valor = libro.ejemplaresDisponibles.toString(),
                color = if (libro.estaDisponible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        when (estadoSolicitud) {
            is EstadoSolicitud.Fallida -> Text(
                text = estadoSolicitud.mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
            is EstadoSolicitud.Exito -> Text(
                text = estadoSolicitud.mensaje,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
            else -> Unit
        }

        Button(
            onClick = onSolicitar,
            enabled = libro.estaDisponible && estadoSolicitud !is EstadoSolicitud.Enviando,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text(if (libro.estaDisponible) "Solicitar préstamo" else "Sin ejemplares disponibles")
        }
    }

    if (estadoSolicitud is EstadoSolicitud.Confirmando || estadoSolicitud is EstadoSolicitud.Enviando) {
        AlertDialog(
            onDismissRequest = onCancelar,
            title = { Text("Confirmar préstamo") },
            text = { Text("¿Deseas solicitar en préstamo \"${libro.titulo}\"? Tendrás 7 días para devolverlo.") },
            confirmButton = {
                Button(onClick = onConfirmar, enabled = estadoSolicitud !is EstadoSolicitud.Enviando) {
                    Text(if (estadoSolicitud is EstadoSolicitud.Enviando) "Enviando..." else "Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = onCancelar, enabled = estadoSolicitud !is EstadoSolicitud.Enviando) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String, color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium)
        Text(valor, style = MaterialTheme.typography.bodyLarge, color = color)
    }
}

@Composable
private fun EstadoCargando(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EstadoNoEncontrado(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No se encontró el libro solicitado")
    }
}
