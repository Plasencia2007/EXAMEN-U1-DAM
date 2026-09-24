package pe.upeu.biblioandes.domain.model

/** SC-C: criterios de ordenamiento del catálogo, resueltos en el dominio. */
enum class OrdenCatalogo(val etiqueta: String) {
    TITULO("Título"),
    ANIO("Año")
}
