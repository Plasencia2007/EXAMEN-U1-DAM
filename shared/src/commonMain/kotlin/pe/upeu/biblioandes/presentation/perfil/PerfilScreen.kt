package pe.upeu.biblioandes.presentation.perfil

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.presentation.components.Eyebrow
import pe.upeu.biblioandes.presentation.theme.LocalBiblioAndesColors

/**
 * RF-06: datos del estudiante y conmutador de tema. No tiene ViewModel propio
 * (la estructura de paquetes esperada solo pide PerfilScreen.kt): el dato del
 * estudiante es una lectura simple y el estado del tema vive en App.kt, que
 * es quien realmente decide los colores de toda la aplicación.
 */
@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    temaOscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    repository: BibliotecaRepository = koinInject()
) {
    val colores = LocalBiblioAndesColors.current
    var estudiante by remember { mutableStateOf<Estudiante?>(null) }

    LaunchedEffect(Unit) {
        estudiante = repository.obtenerEstudiante()
    }

    val datosEstudiante = estudiante
    Column(modifier = modifier.fillMaxSize().background(colores.bg)) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Eyebrow("Tu cuenta")
            Text("Perfil", style = MaterialTheme.typography.headlineLarge, color = colores.ink)
        }

        if (datosEstudiante == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colores.primary)
            }
            return@Column
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaPerfil(datosEstudiante)

            Text(
                "Ajustes",
                style = MaterialTheme.typography.titleLarge,
                color = colores.ink,
                modifier = Modifier.padding(top = 4.dp)
            )

            FilaAjusteTema(temaOscuro, onCambiarTema)
        }
    }
}

@Composable
private fun TarjetaPerfil(estudiante: Estudiante) {
    val colores = LocalBiblioAndesColors.current
    val iniciales = estudiante.nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colores.surface)
            .border(1.dp, colores.line2, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(colores.hero).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(colores.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales, color = colores.onPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(estudiante.nombre, style = MaterialTheme.typography.titleLarge, color = colores.ink)
                Text(estudiante.carrera, color = colores.ink2, fontSize = 14.sp)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            FilaDato(Icons.Default.Badge, "Código de estudiante", estudiante.codigo, mostrarDivisor = true)
            FilaDato(Icons.Default.Email, "Correo", estudiante.correo, mostrarDivisor = false)
        }
    }
}

@Composable
private fun FilaDato(icono: androidx.compose.ui.graphics.vector.ImageVector, etiqueta: String, valor: String, mostrarDivisor: Boolean) {
    val colores = LocalBiblioAndesColors.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(colores.pSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colores.pText, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(etiqueta, color = colores.ink2, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(valor, color = colores.ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        if (mostrarDivisor) {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colores.line2))
        }
    }
}

@Composable
private fun FilaAjusteTema(temaOscuro: Boolean, onCambiarTema: (Boolean) -> Unit) {
    val colores = LocalBiblioAndesColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colores.surface)
            .border(1.dp, colores.line2, RoundedCornerShape(20.dp))
            .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(colores.pSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.DarkMode, contentDescription = null, tint = colores.pText)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Tema oscuro", color = colores.ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Menos brillo, ideal de noche", color = colores.ink2, fontSize = 13.sp)
        }
        InterruptorTema(temaOscuro, onCambiarTema)
    }
}

@Composable
private fun InterruptorTema(activo: Boolean, onCambiar: (Boolean) -> Unit) {
    val colores = LocalBiblioAndesColors.current
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(if (activo) colores.primary else colores.track)
            .border(if (activo) 0.dp else 1.dp, colores.dashed, RoundedCornerShape(17.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCambiar(!activo) }
            .padding(3.dp),
        contentAlignment = if (activo) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(if (activo) colores.onPrimary else colores.ink3)
        )
    }
}
