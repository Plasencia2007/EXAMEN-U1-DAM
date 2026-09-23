package pe.upeu.biblioandes.presentation.catalogo

import pe.upeu.biblioandes.domain.model.Libro

/** Estados de interfaz del catálogo (RF-02): carga, contenido, vacío y error. */
sealed interface CatalogoUiState {
    data object Cargando : CatalogoUiState

    data class Contenido(
        val categorias: List<String>,
        val categoriaSeleccionada: String?,
        val textoBusqueda: String,
        val libros: List<Libro>
    ) : CatalogoUiState {
        val estaVacio: Boolean get() = libros.isEmpty()
    }

    data class Error(val mensaje: String) : CatalogoUiState
}
