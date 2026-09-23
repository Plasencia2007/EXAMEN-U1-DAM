package pe.upeu.biblioandes.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Prestamo

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit,
    viewModel: InicioViewModel = koinViewModel()
) {
    when (val estado = viewModel.uiState) {
        is InicioUiState.Cargando -> EstadoCargando(modifier)
        is InicioUiState.Contenido -> InicioContenido(
            modifier = modifier,
            estudiante = estado.estudiante,
            proximoAVencer = estado.proximoAVencer,
            onIrACatalogo = onIrACatalogo,
            onIrAPrestamos = onIrAPrestamos
        )
    }
}

@Composable
private fun InicioContenido(
    modifier: Modifier,
    estudiante: Estudiante,
    proximoAVencer: Prestamo?,
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Hola, ${estudiante.nombre}", style = MaterialTheme.typography.headlineSmall)
        Text(estudiante.carrera, style = MaterialTheme.typography.bodyMedium)

        if (proximoAVencer != null) {
            TarjetaProximoVencimiento(proximoAVencer, Modifier.padding(top = 24.dp))
        } else {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                Text(
                    "No tienes préstamos activos por el momento",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onIrACatalogo, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.MenuBook, contentDescription = null)
                Text(" Catálogo")
            }
            OutlinedButton(onClick = onIrAPrestamos, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.AutoStories, contentDescription = null)
                Text(" Mis préstamos")
            }
        }
    }
}

@Composable
private fun TarjetaProximoVencimiento(prestamo: Prestamo, modifier: Modifier = Modifier) {
    val diasRestantes = (prestamo.estado as? pe.upeu.biblioandes.domain.model.EstadoPrestamo.Activo)?.diasRestantes

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Próximo a vencer",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                prestamo.libro.titulo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                "Límite: ${prestamo.fechaLimite}" + (diasRestantes?.let { " · $it días restantes" } ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun EstadoCargando(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
