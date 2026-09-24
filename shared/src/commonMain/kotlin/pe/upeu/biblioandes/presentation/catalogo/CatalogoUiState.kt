package pe.upeu.biblioandes.presentation.catalogo

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.OrdenCatalogo

/** Estados de interfaz del catálogo (RF-02): carga, contenido, vacío y error. */
sealed interface CatalogoUiState {
    data object Cargando : CatalogoUiState

    data class Contenido(
        val categorias: List<String>,
        val categoriaSeleccionada: String?,
        val textoBusqueda: String,
        val libros: List<Libro>,
        /** Total sin filtrar, para el chip "Todas" (independiente de la búsqueda). */
        val totalLibros: Int,
        /** Conteo por categoría sin filtrar, para el número de cada chip. */
        val conteoPorCategoria: Map<String, Int>,
        /** SC-C: criterio de orden actual; el estado del selector vive aquí, en el UiState. */
        val ordenSeleccionado: OrdenCatalogo = OrdenCatalogo.TITULO
    ) : CatalogoUiState {
        val estaVacio: Boolean get() = libros.isEmpty()
    }

    data class Error(val mensaje: String) : CatalogoUiState
}
