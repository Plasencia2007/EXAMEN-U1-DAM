package pe.upeu.biblioandes.presentation.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

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
    var estudiante by remember { mutableStateOf<Estudiante?>(null) }

    LaunchedEffect(Unit) {
        estudiante = repository.obtenerEstudiante()
    }

    val datosEstudiante = estudiante
    if (datosEstudiante == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(datosEstudiante.nombre, style = MaterialTheme.typography.titleLarge)
                Text(datosEstudiante.codigo, style = MaterialTheme.typography.bodyMedium)
                Text(datosEstudiante.carrera, style = MaterialTheme.typography.bodyMedium)
                Text(datosEstudiante.correo, style = MaterialTheme.typography.bodyMedium)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        Text("Ajustes", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tema oscuro", style = MaterialTheme.typography.bodyLarge)
            Switch(checked = temaOscuro, onCheckedChange = onCambiarTema)
        }
    }
}
