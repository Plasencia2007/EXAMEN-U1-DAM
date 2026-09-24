package pe.upeu.biblioandes.presentation.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.presentation.components.BotonTema
import pe.upeu.biblioandes.presentation.components.Eyebrow
import pe.upeu.biblioandes.presentation.components.LomoMonograma
import pe.upeu.biblioandes.presentation.theme.LocalBiblioAndesColors

@Composable
fun CatalogoScreen(
    modifier: Modifier = Modifier,
    temaOscuro: Boolean = false,
    onCambiarTema: (Boolean) -> Unit = {},
    onLibroSeleccionado: (Libro) -> Unit,
    viewModel: CatalogoViewModel = koinViewModel()
) {
    CatalogoContenido(
        modifier = modifier,
        estado = viewModel.uiState,
        temaOscuro = temaOscuro,
        onCambiarTema = onCambiarTema,
        onBusquedaCambiada = viewModel::actualizarBusqueda,
        onCategoriaSeleccionada = viewModel::seleccionarCategoria,
        onReintentar = viewModel::reintentar,
        onForzarError = viewModel::forzarErrorYRecargar,
        onLibroSeleccionado = onLibroSeleccionado
    )
}

@Composable
private fun CatalogoContenido(
    modifier: Modifier,
    estado: CatalogoUiState,
    temaOscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    onBusquedaCambiada: (String) -> Unit,
    onCategoriaSeleccionada: (String?) -> Unit,
    onReintentar: () -> Unit,
    onForzarError: () -> Unit,
    onLibroSeleccionado: (Libro) -> Unit
) {
    val colores = LocalBiblioAndesColors.current
    Column(modifier = modifier.fillMaxSize().background(colores.bg)) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Eyebrow("Biblioteca")
                    Text("Catálogo", style = MaterialTheme.typography.headlineLarge, color = colores.ink)
                }
                BotonTema(temaOscuro, onCambiarTema)
            }

            if (estado is CatalogoUiState.Contenido) {
                CasillaVacia(20.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    CampoBusqueda(
                        valor = estado.textoBusqueda,
                        onValorCambia = onBusquedaCambiada,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colores.primary)
                            .clickableSimple { onCategoriaSeleccionada(null) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Filtros y orden", tint = colores.onPrimary)
                    }
                }
            }
        }

        when (estado) {
            is CatalogoUiState.Cargando -> EstadoCargando(Modifier.weight(1f))
            is CatalogoUiState.Error -> EstadoError(Modifier.weight(1f), estado.mensaje, onReintentar)
            is CatalogoUiState.Contenido -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        ChipCategoria(
                            etiqueta = "Todas",
                            cantidad = estado.totalLibros,
                            seleccionado = estado.categoriaSeleccionada == null,
                            onClick = { onCategoriaSeleccionada(null) }
                        )
                    }
                    items(estado.categorias) { categoria ->
                        ChipCategoria(
                            etiqueta = categoria,
                            cantidad = estado.conteoPorCategoria[categoria] ?: 0,
                            seleccionado = estado.categoriaSeleccionada == categoria,
                            onClick = { onCategoriaSeleccionada(categoria) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val etiquetaConteo = if (estado.libros.size == 1) "1 título" else "${estado.libros.size} títulos"
                    Text(etiquetaConteo, style = MaterialTheme.typography.labelMedium, color = colores.ink2)
                    Text("Orden: título", style = MaterialTheme.typography.bodySmall, color = colores.ink3)
                }

                if (estado.estaVacio) {
                    EstadoVacio(Modifier.weight(1f))
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estado.libros, key = { it.id }) { libro ->
                            FilaLibro(libro = libro, onClick = { onLibroSeleccionado(libro) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CasillaVacia(altura: Dp) {
    Box(modifier = Modifier.height(altura))
}

@Composable
private fun CampoBusqueda(valor: String, onValorCambia: (String) -> Unit, modifier: Modifier = Modifier) {
    val colores = LocalBiblioAndesColors.current
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colores.surface)
            .border(1.dp, colores.line, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = colores.ink2)
        Box(modifier = Modifier.weight(1f)) {
            if (valor.isEmpty()) {
                Text("Buscar por título o autor", color = colores.ink3, fontSize = 15.sp)
            }
            BasicTextField(
                value = valor,
                onValueChange = onValorCambia,
                singleLine = true,
                textStyle = TextStyle(color = colores.ink, fontSize = 15.sp),
                cursorBrush = SolidColor(colores.primary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ChipCategoria(etiqueta: String, cantidad: Int, seleccionado: Boolean, onClick: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    val fondo = if (seleccionado) colores.chipOn else colores.surface
    val texto = if (seleccionado) colores.onChipOn else colores.ink
    val contorno = if (seleccionado) colores.chipOn else colores.line
    Row(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .border(1.dp, contorno, RoundedCornerShape(20.dp))
            .clickableSimple(onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(etiqueta, color = texto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(
            "$cantidad",
            color = if (seleccionado) texto.copy(alpha = 0.75f) else colores.ink3,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun FilaLibro(libro: Libro, onClick: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colores.surface)
            .border(1.dp, colores.line2, RoundedCornerShape(18.dp))
            .clickableSimple(onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.height(74.dp)) {
            LomoMonograma(libro = libro, ancho = 52.dp, alto = 74.dp)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(libro.titulo, style = MaterialTheme.typography.titleMedium, color = colores.ink)
            Text("${libro.autor} · ${libro.categoria}", style = MaterialTheme.typography.bodyMedium, color = colores.ink2)
            // SC-D: editorial mostrada bajo el autor.
            Text(libro.editorial, style = MaterialTheme.typography.bodySmall, color = colores.ink3)
            InsigniaDisponibilidad(libro, Modifier.padding(top = 2.dp))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = colores.chev)
    }
}

@Composable
private fun InsigniaDisponibilidad(libro: Libro, modifier: Modifier = Modifier) {
    val colores = LocalBiblioAndesColors.current
    val disponible = libro.estaDisponible
    val fondo = if (disponible) colores.okBg else colores.badBg
    val texto = if (disponible) colores.ok else colores.bad
    val etiqueta = when {
        !disponible -> "Sin ejemplares"
        libro.ejemplaresDisponibles == 1 -> "1 disponible"
        else -> "${libro.ejemplaresDisponibles} disponibles"
    }
    Row(
        modifier = modifier
            .height(26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(fondo)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (disponible) Icons.Default.CheckCircle else Icons.Default.RemoveCircleOutline,
            contentDescription = null,
            tint = texto,
            modifier = Modifier.size(14.dp)
        )
        Text(etiqueta, color = texto, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun EstadoCargando(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = LocalBiblioAndesColors.current.primary)
    }
}

@Composable
private fun EstadoVacio(modifier: Modifier) {
    val colores = LocalBiblioAndesColors.current
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.Search, contentDescription = null, tint = colores.ink2)
            Text("Sin resultados", style = MaterialTheme.typography.headlineSmall, color = colores.ink)
            Text("Prueba con otro título, autor o categoría.", color = colores.ink2, fontSize = 14.sp)
        }
    }
}

@Composable
private fun EstadoError(modifier: Modifier, mensaje: String, onReintentar: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(mensaje, style = MaterialTheme.typography.bodyLarge, color = colores.bad)
            Button(onClick = onReintentar) { Text("Reintentar") }
        }
    }
}

@Composable
private fun Modifier.clickableSimple(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)
