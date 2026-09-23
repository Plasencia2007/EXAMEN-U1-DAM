package pe.upeu.biblioandes.presentation.catalogo

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
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.model.Libro

@Composable
fun CatalogoScreen(
    modifier: Modifier = Modifier,
    onLibroSeleccionado: (Libro) -> Unit,
    viewModel: CatalogoViewModel = koinViewModel()
) {
    CatalogoContenido(
        modifier = modifier,
        estado = viewModel.uiState,
        onBusquedaCambiada = viewModel::actualizarBusqueda,
        onCategoriaSeleccionada = viewModel::seleccionarCategoria,
        onReintentar = viewModel::reintentar,
        onForzarError = viewModel::forzarErrorYRecargar,
        onLibroSeleccionado = onLibroSeleccionado
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogoContenido(
    modifier: Modifier,
    estado: CatalogoUiState,
    onBusquedaCambiada: (String) -> Unit,
    onCategoriaSeleccionada: (String?) -> Unit,
    onReintentar: () -> Unit,
    onForzarError: () -> Unit,
    onLibroSeleccionado: (Libro) -> Unit
) {
    when (estado) {
        is CatalogoUiState.Cargando -> EstadoCargando(modifier)
        is CatalogoUiState.Error -> EstadoError(modifier, estado.mensaje, onReintentar)
        is CatalogoUiState.Contenido -> Column(modifier = modifier.fillMaxSize()) {
            OutlinedTextField(
                value = estado.textoBusqueda,
                onValueChange = onBusquedaCambiada,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Buscar por título o autor") }
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = estado.categoriaSeleccionada == null,
                        onClick = { onCategoriaSeleccionada(null) },
                        label = { Text("Todas") }
                    )
                }
                items(estado.categorias) { categoria ->
                    FilterChip(
                        selected = estado.categoriaSeleccionada == categoria,
                        onClick = { onCategoriaSeleccionada(categoria) },
                        label = { Text(categoria) }
                    )
                }
                item {
                    FilterChip(
                        selected = false,
                        onClick = onForzarError,
                        label = { Text("Simular error") }
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
                    items(estado.libros, key = { it.id }) { libro ->
                        TarjetaLibro(libro = libro, onClick = { onLibroSeleccionado(libro) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaLibro(libro: Libro, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(libro.titulo, style = MaterialTheme.typography.titleMedium)
            Text(libro.autor, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = when {
                    !libro.estaDisponible -> "Sin ejemplares disponibles"
                    libro.ejemplaresDisponibles == 1 -> "1 ejemplar disponible"
                    else -> "${libro.ejemplaresDisponibles} ejemplares disponibles"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (libro.estaDisponible) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
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

@Composable
private fun EstadoVacio(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.MenuBook, contentDescription = null)
            Text("No se encontraron libros con ese filtro")
        }
    }
}

@Composable
private fun EstadoError(modifier: Modifier, mensaje: String, onReintentar: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(mensaje, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onReintentar, modifier = Modifier.padding(top = 12.dp)) {
                Text("Reintentar")
            }
        }
    }
}
