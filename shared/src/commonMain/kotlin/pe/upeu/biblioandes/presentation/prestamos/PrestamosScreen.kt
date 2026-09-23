package pe.upeu.biblioandes.presentation.prestamos

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.DURACION_PRESTAMO_DIAS
import pe.upeu.biblioandes.presentation.components.BotonTema
import pe.upeu.biblioandes.presentation.components.Eyebrow
import pe.upeu.biblioandes.presentation.components.LomoMonograma
import pe.upeu.biblioandes.presentation.inicio.fechaCortaDe
import pe.upeu.biblioandes.presentation.theme.LocalBiblioAndesColors

@Composable
fun PrestamosScreen(
    modifier: Modifier = Modifier,
    temaOscuro: Boolean = false,
    onCambiarTema: (Boolean) -> Unit = {},
    viewModel: PrestamosViewModel = koinViewModel()
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
                    Eyebrow("Tu cuenta")
                    Text("Mis préstamos", style = MaterialTheme.typography.headlineLarge, color = colores.ink)
                }
                BotonTema(temaOscuro, onCambiarTema)
            }

            val estadoActual = viewModel.uiState
            if (estadoActual is PrestamosUiState.Contenido) {
                Box(modifier = Modifier.height(20.dp))
                SegmentoFiltros(estadoActual, onSeleccionar = viewModel::seleccionarFiltro)
            }
        }

        when (val estado = viewModel.uiState) {
            is PrestamosUiState.Cargando -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colores.primary)
            }
            is PrestamosUiState.Contenido -> {
                if (estado.estaVacio) {
                    EstadoVacio(Modifier.weight(1f))
                } else {
                    val grupos = agruparPorEstado(estado.prestamos)
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        grupos.forEach { grupo ->
                            item(key = "titulo-${grupo.titulo}") {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(grupo.titulo, style = MaterialTheme.typography.titleLarge, color = colores.ink)
                                    Box(
                                        modifier = Modifier
                                            .height(24.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(colores.track)
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${grupo.items.size}", color = colores.ink2, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            items(grupo.items, key = { it.id }) { prestamo ->
                                when (prestamo.estado) {
                                    is EstadoPrestamo.Vencido -> TarjetaVencido(
                                        prestamo,
                                        enDevolucion = prestamo.id == viewModel.prestamoIdEnProceso,
                                        onDevolver = { viewModel.devolver(prestamo.id) }
                                    )
                                    is EstadoPrestamo.Activo -> TarjetaActivo(
                                        prestamo,
                                        enDevolucion = prestamo.id == viewModel.prestamoIdEnProceso,
                                        onDevolver = { viewModel.devolver(prestamo.id) }
                                    )
                                    is EstadoPrestamo.Devuelto -> FilaDevuelto(prestamo)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private class Grupo(val titulo: String, val items: List<Prestamo>)

private fun agruparPorEstado(prestamos: List<Prestamo>): List<Grupo> {
    val vencidos = prestamos.filter { it.estado is EstadoPrestamo.Vencido }
    val activos = prestamos.filter { it.estado is EstadoPrestamo.Activo }
    val devueltos = prestamos.filter { it.estado is EstadoPrestamo.Devuelto }
    return listOfNotNull(
        if (vencidos.isNotEmpty()) Grupo("Requiere atención", vencidos) else null,
        if (activos.isNotEmpty()) Grupo("En curso", activos) else null,
        if (devueltos.isNotEmpty()) Grupo("Historial", devueltos) else null
    )
}

@Composable
private fun SegmentoFiltros(estado: PrestamosUiState.Contenido, onSeleccionar: (FiltroEstado) -> Unit) {
    val colores = LocalBiblioAndesColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colores.track)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FiltroEstado.entries.forEach { filtro ->
            val seleccionado = filtro == estado.filtroSeleccionado
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (seleccionado) colores.segOn else androidx.compose.ui.graphics.Color.Transparent)
                    .clickableSinIndicacion { onSeleccionar(filtro) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    filtro.etiqueta,
                    color = if (seleccionado) colores.ink else colores.ink2,
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    " ${estado.conteoDe(filtro)}",
                    color = if (seleccionado) colores.pText else colores.ink3,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun TarjetaVencido(prestamo: Prestamo, enDevolucion: Boolean, onDevolver: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    val diasAtraso = (prestamo.estado as EstadoPrestamo.Vencido).diasDeAtraso
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colores.surface)
            .border(1.dp, colores.badLine, RoundedCornerShape(18.dp))
    ) {
        EncabezadoTarjetaPrestamo(prestamo, "Vencido", colores.bad, colores.onBad)
        Row(
            modifier = Modifier.fillMaxWidth().background(colores.badBg).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = colores.bad, modifier = Modifier.size(18.dp))
            Text(
                "$diasAtraso días de atraso · devuélvelo cuanto antes",
                color = colores.bad,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
        }
        BotonDevolver(enDevolucion, onDevolver, colores.bad, Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
    }
}

@Composable
private fun TarjetaActivo(prestamo: Prestamo, enDevolucion: Boolean, onDevolver: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    val diasRestantes = (prestamo.estado as EstadoPrestamo.Activo).diasRestantes
    val progreso = ((DURACION_PRESTAMO_DIAS - diasRestantes).toFloat() / DURACION_PRESTAMO_DIAS).coerceIn(0f, 1f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colores.surface)
            .border(1.dp, colores.line2, RoundedCornerShape(18.dp))
            .padding(bottom = 6.dp)
    ) {
        EncabezadoTarjetaPrestamo(prestamo, "Activo", colores.pSoft, colores.pText)
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(colores.track)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(progreso).height(6.dp).clip(RoundedCornerShape(3.dp)).background(colores.primary)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = colores.pText, modifier = Modifier.size(15.dp))
                    Text("Quedan $diasRestantes días", color = colores.pText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text("Devolver antes del ${fechaCortaDe(prestamo.fechaLimite)}", color = colores.ink2, fontSize = 13.sp)
            }
        }
        BotonDevolver(enDevolucion, onDevolver, colores.primary, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }
}

@Composable
private fun EncabezadoTarjetaPrestamo(prestamo: Prestamo, etiquetaEstado: String, fondoInsignia: androidx.compose.ui.graphics.Color, textoInsignia: androidx.compose.ui.graphics.Color) {
    val colores = LocalBiblioAndesColors.current
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(modifier = Modifier.height(64.dp)) {
            LomoMonograma(prestamo.libro, ancho = 44.dp, alto = 64.dp)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(
                    prestamo.libro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = colores.ink,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(fondoInsignia)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(etiquetaEstado, color = textoInsignia, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            Text(prestamo.libro.autor, color = colores.ink2, fontSize = 14.sp)
            Text(
                "Prestado ${fechaCortaDe(prestamo.fechaPrestamo)} · Límite ${fechaCortaDe(prestamo.fechaLimite)}",
                color = colores.ink2,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun BotonDevolver(enDevolucion: Boolean, onDevolver: () -> Unit, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickableSinIndicacion(habilitado = !enDevolucion, onClick = onDevolver),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (enDevolucion) "Registrando devolución..." else "Devolver",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun FilaDevuelto(prestamo: Prestamo) {
    val colores = LocalBiblioAndesColors.current
    val fechaDevolucion = (prestamo.estado as EstadoPrestamo.Devuelto).fechaDevolucion
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, colores.line, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(width = 36.dp, height = 52.dp).clip(RoundedCornerShape(6.dp)).background(colores.line2))
        Column(modifier = Modifier.weight(1f)) {
            Text(prestamo.libro.titulo, style = MaterialTheme.typography.titleSmall, color = colores.ink)
            Text(
                "${prestamo.libro.autor} · ${fechaCortaDe(prestamo.fechaPrestamo)} – ${fechaCortaDe(prestamo.fechaLimite)}",
                color = colores.ink2,
                fontSize = 13.sp
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colores.ok, modifier = Modifier.size(16.dp))
            Text(fechaCortaDe(fechaDevolucion), color = colores.ok, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EstadoVacio(modifier: Modifier) {
    val colores = LocalBiblioAndesColors.current
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = colores.ink2)
            Text("No tienes préstamos con ese filtro", color = colores.ink2)
        }
    }
}

@Composable
private fun Modifier.clickableSinIndicacion(habilitado: Boolean = true, onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = habilitado,
    onClick = onClick
)
