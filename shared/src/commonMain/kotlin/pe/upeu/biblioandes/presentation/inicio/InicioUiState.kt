package pe.upeu.biblioandes.presentation.inicio

import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Prestamo

/** Estados de interfaz de Inicio (RF-01): carga y contenido. */
sealed interface InicioUiState {
    data object Cargando : InicioUiState

    data class Contenido(
        val estudiante: Estudiante,
        val fechaHoy: String,
        /** El préstamo Activo cuya devolución vence primero, si tiene alguno. */
        val proximoAVencer: Prestamo?,
        val prestamosVencidos: List<Prestamo>,
        val totalLibros: Int,
        val prestamosActivosCount: Int
    ) : InicioUiState
}
