package pe.upeu.biblioandes.presentation.catalogo

private val TILDES = mapOf(
    'á' to 'a', 'é' to 'e', 'í' to 'i', 'ó' to 'o', 'ú' to 'u', 'ü' to 'u'
)

/** Minúsculas y sin tildes, para comparar sin distinguir mayúsculas ni tildes (RF-05). */
fun normalizarTexto(texto: String): String =
    texto.lowercase().map { TILDES[it] ?: it }.joinToString(separator = "")
