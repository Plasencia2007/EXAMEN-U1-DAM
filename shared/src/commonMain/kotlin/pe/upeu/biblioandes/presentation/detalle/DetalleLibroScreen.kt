package pe.upeu.biblioandes.presentation.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.usecase.DURACION_PRESTAMO_DIAS
import pe.upeu.biblioandes.domain.usecase.hoyLocal
import pe.upeu.biblioandes.presentation.components.LomoPortada
import pe.upeu.biblioandes.presentation.inicio.fechaCortaDe
import pe.upeu.biblioandes.presentation.theme.LocalBiblioAndesColors

@Composable
fun DetalleLibroScreen(
    libroId: Int,
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {},
    onVerPrestamos: () -> Unit = {},
    viewModel: DetalleLibroViewModel = koinViewModel(key = "detalle-$libroId") { parametersOf(libroId) }
) {
    val estadoSolicitud = viewModel.estadoSolicitud
    val colores = LocalBiblioAndesColors.current

    when (val estado = viewModel.uiState) {
        is DetalleLibroUiState.Cargando -> Box(modifier = modifier.fillMaxSize().background(colores.bg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colores.primary)
        }
        is DetalleLibroUiState.NoEncontrado -> Box(modifier = modifier.fillMaxSize().background(colores.bg), contentAlignment = Alignment.Center) {
            Text("No se encontró el libro solicitado", color = colores.ink)
        }
        is DetalleLibroUiState.Contenido -> DetalleContenido(
            modifier = modifier,
            libro = estado.libro,
            estadoSolicitud = estadoSolicitud,
            onVolver = onVolver,
            onVerPrestamos = onVerPrestamos,
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
    onVolver: () -> Unit,
    onVerPrestamos: () -> Unit,
    onSolicitar: () -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val colores = LocalBiblioAndesColors.current
    val yaSolicitado = estadoSolicitud is EstadoSolicitud.Exito

    Column(modifier = modifier.fillMaxSize().background(colores.bg)) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colores.hero, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BotonCircular(Icons.AutoMirrored.Filled.ArrowBack, "Volver al catálogo", onVolver)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BotonCircular(Icons.Outlined.BookmarkBorder, "Guardar en favoritos") {}
                        BotonCircular(Icons.Default.Share, "Compartir") {}
                    }
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LomoPortada(libro, ancho = 136.dp, alto = 198.dp)
                }
            }

            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        libro.categoria.uppercase(),
                        color = colores.accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.7.sp
                    )
                    Text(libro.titulo, style = MaterialTheme.typography.headlineMedium, color = colores.ink)
                    Text("por ${libro.autor}", color = colores.ink2, fontSize = 16.sp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (libro.estaDisponible) colores.okBg else colores.badBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(colores.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (libro.estaDisponible) Icons.Default.CheckCircle else Icons.Default.RemoveCircleOutline,
                            contentDescription = null,
                            tint = if (libro.estaDisponible) colores.ok else colores.bad
                        )
                    }
                    Column {
                        val texto = if (libro.estaDisponible) {
                            "${libro.ejemplaresDisponibles} ejemplares disponibles"
                        } else {
                            "Sin ejemplares disponibles"
                        }
                        Text(texto, color = if (libro.estaDisponible) colores.ok else colores.bad, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("En ${libro.sede}", color = if (libro.estaDisponible) colores.ok else colores.bad, fontSize = 13.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TarjetaDato(Icons.Default.CalendarMonth, "Año", libro.anio.toString(), Modifier.weight(1f))
                    TarjetaDato(Icons.Default.Sell, "Categoría", libro.categoria, Modifier.weight(1f))
                    TarjetaDato(Icons.Default.LocationOn, "Sede", libro.sede, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colores.dashed, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = colores.pText, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Préstamo por $DURACION_PRESTAMO_DIAS días", color = colores.ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        val fechaLimite = remember(libro.id) {
                            fechaCortaDe(hoyLocal().plus(DURACION_PRESTAMO_DIAS, DateTimeUnit.DAY).toString())
                        }
                        Text(
                            "Si lo solicitas hoy, la fecha límite de devolución es el $fechaLimite.",
                            color = colores.ink2,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                if (estadoSolicitud is EstadoSolicitud.Fallida) {
                    Text(estadoSolicitud.mensaje, color = colores.bad, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colores.surface)
                .border(width = 1.dp, color = colores.line)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            if (yaSolicitado) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colores.okBg)
                        .clickableSinIndicacion(onClick = onVerPrestamos),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colores.ok, modifier = Modifier.size(20.dp))
                    Box(modifier = Modifier.width(10.dp))
                    Text("Solicitado · Ver mis préstamos", color = colores.ok, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                val habilitado = libro.estaDisponible && estadoSolicitud !is EstadoSolicitud.Enviando
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (habilitado) colores.primary else colores.track)
                        .clickableSinIndicacion(habilitado = habilitado, onClick = onSolicitar),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            !libro.estaDisponible -> "Sin ejemplares disponibles"
                            estadoSolicitud is EstadoSolicitud.Enviando -> "Enviando..."
                            else -> "Solicitar préstamo"
                        },
                        color = if (habilitado) colores.onPrimary else colores.ink3,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (estadoSolicitud is EstadoSolicitud.Confirmando || estadoSolicitud is EstadoSolicitud.Enviando) {
        DialogoConfirmacion(libro, estadoSolicitud, onConfirmar, onCancelar)
    }
}

@Composable
private fun DialogoConfirmacion(libro: Libro, estadoSolicitud: EstadoSolicitud, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = colores.surface,
        title = { Text("Confirmar préstamo", color = colores.ink, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "¿Deseas solicitar en préstamo \"${libro.titulo}\"? Tendrás $DURACION_PRESTAMO_DIAS días para devolverlo.",
                color = colores.ink2
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirmar, enabled = estadoSolicitud !is EstadoSolicitud.Enviando) {
                Text(
                    if (estadoSolicitud is EstadoSolicitud.Enviando) "Enviando..." else "Confirmar",
                    color = colores.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onCancelar, enabled = estadoSolicitud !is EstadoSolicitud.Enviando) {
                Text("Cancelar", color = colores.ink2)
            }
        }
    )
}

@Composable
private fun BotonCircular(icono: ImageVector, descripcion: String, onClick: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(colores.surface)
            .clickableSinIndicacion(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = descripcion, tint = colores.ink)
    }
}

@Composable
private fun TarjetaDato(icono: ImageVector, etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    val colores = LocalBiblioAndesColors.current
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(colores.surface)
            .border(1.dp, colores.line2, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icono, contentDescription = null, tint = colores.ink2, modifier = Modifier.size(18.dp))
        Column {
            Text(etiqueta, color = colores.ink2, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(valor, color = colores.ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
