package pe.upeu.biblioandes.presentation.prestamos

import pe.upeu.biblioandes.domain.model.Prestamo

/** Filtros de estado disponibles para "Mis préstamos" (RF-04). */
enum class FiltroEstado(val etiqueta: String) {
    TODOS("Todos"),
    ACTIVO("Activos"),
    VENCIDO("Vencidos"),
    DEVUELTO("Devueltos")
}

/** Estados de interfaz de la lista de préstamos: carga, contenido y lista vacía. */
sealed interface PrestamosUiState {
    data object Cargando : PrestamosUiState

    data class Contenido(
        val filtroSeleccionado: FiltroEstado,
        val prestamos: List<Prestamo>,
        /** Conteos sin filtrar, para el número de cada pestaña. */
        val totalCount: Int,
        val activoCount: Int,
        val vencidoCount: Int,
        val devueltoCount: Int
    ) : PrestamosUiState {
        val estaVacio: Boolean get() = prestamos.isEmpty()

        fun conteoDe(filtro: FiltroEstado): Int = when (filtro) {
            FiltroEstado.TODOS -> totalCount
            FiltroEstado.ACTIVO -> activoCount
            FiltroEstado.VENCIDO -> vencidoCount
            FiltroEstado.DEVUELTO -> devueltoCount
        }
    }
}
