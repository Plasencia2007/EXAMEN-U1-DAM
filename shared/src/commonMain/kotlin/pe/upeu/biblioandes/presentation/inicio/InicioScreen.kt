package pe.upeu.biblioandes.presentation.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
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
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.DURACION_PRESTAMO_DIAS
import pe.upeu.biblioandes.presentation.components.AvatarBoton
import pe.upeu.biblioandes.presentation.components.BotonTema
import pe.upeu.biblioandes.presentation.components.Eyebrow
import pe.upeu.biblioandes.presentation.components.LomoMonograma
import pe.upeu.biblioandes.presentation.theme.LocalBiblioAndesColors

private fun inicialesDe(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    temaOscuro: Boolean = false,
    onCambiarTema: (Boolean) -> Unit = {},
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit,
    onIrAPerfil: () -> Unit = {},
    viewModel: InicioViewModel = koinViewModel()
) {
    val colores = LocalBiblioAndesColors.current
    when (val estado = viewModel.uiState) {
        is InicioUiState.Cargando -> Box(modifier = modifier.fillMaxSize().background(colores.bg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colores.primary)
        }
        is InicioUiState.Contenido -> InicioContenido(
            modifier = modifier,
            estado = estado,
            temaOscuro = temaOscuro,
            onCambiarTema = onCambiarTema,
            onIrACatalogo = onIrACatalogo,
            onIrAPrestamos = onIrAPrestamos,
            onIrAPerfil = onIrAPerfil
        )
    }
}

@Composable
private fun InicioContenido(
    modifier: Modifier,
    estado: InicioUiState.Contenido,
    temaOscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit,
    onIrAPerfil: () -> Unit
) {
    val colores = LocalBiblioAndesColors.current
    Column(modifier = modifier.fillMaxSize().background(colores.bg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Eyebrow(estado.fechaHoy)
                Text(
                    "Hola, ${estado.estudiante.nombre.substringBefore(" ")}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colores.ink
                )
                Text(estado.estudiante.carrera, color = colores.ink2, fontSize = 15.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonTema(temaOscuro, onCambiarTema)
                AvatarBoton(inicialesDe(estado.estudiante.nombre), onClick = onIrAPerfil)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (estado.proximoAVencer != null) {
                TarjetaProximoAVencer(estado.proximoAVencer, onClick = onIrAPrestamos)
            }
            if (estado.prestamosVencidos.isNotEmpty()) {
                BannerVencidos(estado.prestamosVencidos, onClick = onIrAPrestamos)
            }
            if (estado.proximoAVencer == null && estado.prestamosVencidos.isEmpty()) {
                Text(
                    "No tienes préstamos activos por el momento",
                    color = colores.ink2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colores.surface)
                        .border(1.dp, colores.line2, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                )
            }

            Text(
                "Accesos rápidos",
                style = MaterialTheme.typography.titleLarge,
                color = colores.ink,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                TarjetaAcceso(
                    modifier = Modifier.weight(1f),
                    icono = Icons.AutoMirrored.Filled.MenuBook,
                    titulo = "Catálogo",
                    subtitulo = "${estado.totalLibros} títulos",
                    onClick = onIrACatalogo
                )
                TarjetaAcceso(
                    modifier = Modifier.weight(1f),
                    icono = Icons.Default.AutoStories,
                    titulo = "Mis préstamos",
                    subtitulo = "${estado.prestamosActivosCount} activos" +
                        if (estado.prestamosVencidos.isNotEmpty()) " · ${estado.prestamosVencidos.size} vencido${if (estado.prestamosVencidos.size > 1) "s" else ""}" else "",
                    onClick = onIrAPrestamos
                )
            }

            Box(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TarjetaProximoAVencer(prestamo: Prestamo, onClick: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    val diasRestantes = (prestamo.estado as? EstadoPrestamo.Activo)?.diasRestantes ?: 0
    val progreso = ((DURACION_PRESTAMO_DIAS - diasRestantes).toFloat() / DURACION_PRESTAMO_DIAS).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colores.feature)
            .clickableSinIndicacion(onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = colores.feature2, modifier = Modifier.size(16.dp))
                Text("PRÓXIMO A VENCER", color = colores.feature2, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
            }
            Box(
                modifier = Modifier
                    .height(26.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(colores.featTrack)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("$diasRestantes días", color = colores.onFeature, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.height(74.dp)) {
                LomoMonograma(prestamo.libro, ancho = 52.dp, alto = 74.dp)
            }
            Column {
                Text(prestamo.libro.titulo, color = colores.onFeature, style = MaterialTheme.typography.titleLarge)
                Text(prestamo.libro.autor, color = colores.feature2, fontSize = 14.sp)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(colores.featTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progreso)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(colores.featBar)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Límite: ${fechaCortaDe(prestamo.fechaLimite)}", color = colores.feature2, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Ver préstamo", color = colores.onFeature, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = colores.onFeature, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BannerVencidos(vencidos: List<Prestamo>, onClick: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    val primero = vencidos.first()
    val diasAtraso = (primero.estado as? EstadoPrestamo.Vencido)?.diasDeAtraso ?: 0
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colores.badBg)
            .clickableSinIndicacion(onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(colores.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = colores.bad)
        }
        Column(modifier = Modifier.weight(1f)) {
            val titulo = if (vencidos.size == 1) "1 préstamo vencido" else "${vencidos.size} préstamos vencidos"
            Text(titulo, color = colores.bad, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("${primero.libro.titulo} · $diasAtraso días de atraso", color = colores.bad, fontSize = 13.sp)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = colores.bad)
    }
}

@Composable
private fun TarjetaAcceso(modifier: Modifier, icono: androidx.compose.ui.graphics.vector.ImageVector, titulo: String, subtitulo: String, onClick: () -> Unit) {
    val colores = LocalBiblioAndesColors.current
    Column(
        modifier = modifier
            .aspectRatio(0.93f)
            .clip(RoundedCornerShape(20.dp))
            .background(colores.surface)
            .border(1.dp, colores.line2, RoundedCornerShape(20.dp))
            .clickableSinIndicacion(onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(colores.pSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = colores.pText)
        }
        Column {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = colores.ink)
            Text(subtitulo, color = colores.ink2, fontSize = 13.sp)
        }
    }
}

@Composable
private fun Modifier.clickableSinIndicacion(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick
)
