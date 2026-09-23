package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.upeu.biblioandes.presentation.theme.LocalBiblioAndesColors

/** Botón circular de sol/luna, replicado en la cabecera de cada pantalla principal (no solo en Perfil). */
@Composable
fun BotonTema(temaOscuro: Boolean, onCambiarTema: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colores = LocalBiblioAndesColors.current
    IconButton(
        onClick = { onCambiarTema(!temaOscuro) },
        modifier = modifier
            .size(44.dp)
            .background(colores.surface, CircleShape)
            .border(1.dp, colores.line, CircleShape)
    ) {
        Icon(
            imageVector = if (temaOscuro) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = if (temaOscuro) "Cambiar a modo claro" else "Cambiar a modo oscuro",
            tint = colores.ink
        )
    }
}

/** Avatar circular con iniciales, usado en Inicio para entrar a Perfil. */
@Composable
fun AvatarBoton(iniciales: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colores = LocalBiblioAndesColors.current
    Box(
        modifier = modifier
            .size(44.dp)
            .background(colores.primary, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciales, color = colores.onPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

/** Eyebrow (etiqueta pequeña en mayúsculas) sobre un título, patrón repetido en toda la app. */
@Composable
fun Eyebrow(texto: String, color: Color = LocalBiblioAndesColors.current.accent) {
    Text(
        text = texto.uppercase(),
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.7.sp
    )
}
