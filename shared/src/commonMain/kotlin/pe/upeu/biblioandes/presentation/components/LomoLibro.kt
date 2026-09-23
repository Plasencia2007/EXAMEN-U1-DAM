package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.presentation.theme.FeatBarLight
import pe.upeu.biblioandes.presentation.theme.TapaGestion
import pe.upeu.biblioandes.presentation.theme.TapaLiteratura
import pe.upeu.biblioandes.presentation.theme.TapaMatematica
import pe.upeu.biblioandes.presentation.theme.TapaProgramacion
import pe.upeu.biblioandes.presentation.theme.TapaProgramacionAlt
import pe.upeu.biblioandes.presentation.theme.TapaRedes
import pe.upeu.biblioandes.presentation.theme.TapaRedesAlt

private val PALETA_TAPAS = listOf(
    TapaProgramacion, TapaProgramacionAlt, TapaMatematica, TapaRedes,
    TapaRedesAlt, TapaGestion, TapaLiteratura, Color(0xFF4A5568)
)

private val MONOGRAMAS_FIJOS = mapOf(
    1 to "Kt", 2 to "Ed", 3 to "Ca", 4 to "Rc", 5 to "Sr", 6 to "Gp"
)

private val STOPWORDS = setOf("de", "del", "la", "el", "los", "las", "y", "en", "un", "una", "por")

/** Color de tapa determinístico por libro: fijo para el anexo, cíclico para el resto. */
fun colorTapaDe(libro: Libro): Color =
    PALETA_TAPAS[(libro.id - 1).mod(PALETA_TAPAS.size)]

/** Iniciales de lomo: fijas para los 6 libros del anexo (calcadas del prototipo), algorítmicas para el resto. */
fun monogramaDe(libro: Libro): String {
    MONOGRAMAS_FIJOS[libro.id]?.let { return it }
    val palabras = libro.titulo.split(" ").filter { it.lowercase() !in STOPWORDS && it.isNotBlank() }
    val primera = palabras.firstOrNull()?.firstOrNull()?.uppercaseChar() ?: '?'
    val segunda = (palabras.getOrNull(1) ?: palabras.firstOrNull()?.drop(1))?.firstOrNull()?.lowercaseChar() ?: ' '
    return "$primera$segunda"
}

private val FormaLomo = RoundedCornerShape(topStart = 3.dp, topEnd = 7.dp, bottomEnd = 7.dp, bottomStart = 3.dp)

/** Lomo pequeño con monograma: usado en listas (catálogo, mis préstamos). */
@Composable
fun LomoMonograma(libro: Libro, ancho: Dp, alto: Dp, modifier: Modifier = Modifier, mostrarLinea: Boolean = true) {
    val color = colorTapaDe(libro)
    Box(
        modifier = modifier
            .width(ancho)
            .fillMaxHeight()
            .clip(FormaLomo)
            .background(color)
    ) {
        Box(
            modifier = Modifier.fillMaxHeight().width(4.dp).background(Color.Black.copy(alpha = 0.18f))
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 11.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            if (mostrarLinea) {
                Box(modifier = Modifier.width(14.dp).background(FeatBarLight).height(2.dp))
            }
            Text(
                text = monogramaDe(libro),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (alto > 70.dp) 19.sp else 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}

/** Portada grande con autor y título: usada en el detalle del libro. */
@Composable
fun LomoPortada(libro: Libro, ancho: Dp, alto: Dp, modifier: Modifier = Modifier) {
    val color = colorTapaDe(libro)
    Box(
        modifier = modifier
            .width(ancho)
            .height(alto)
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 10.dp, bottomEnd = 10.dp, bottomStart = 4.dp))
            .background(color)
    ) {
        Box(modifier = Modifier.fillMaxHeight().width(7.dp).background(Color.Black.copy(alpha = 0.2f)))
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 22.dp, end = 14.dp, top = 18.dp, bottom = 16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(
                text = libro.autor.uppercase(),
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 1.6.sp
            )
            Text(
                text = libro.titulo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Box(modifier = Modifier.width(26.dp).background(FeatBarLight).height(2.dp))
        }
    }
}
