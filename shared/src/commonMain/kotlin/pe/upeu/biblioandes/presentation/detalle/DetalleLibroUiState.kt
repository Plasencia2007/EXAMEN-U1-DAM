package pe.upeu.biblioandes.presentation.detalle

import pe.upeu.biblioandes.domain.model.Libro

/** Estados de interfaz del detalle de un libro (RF-03): carga, contenido y no encontrado. */
sealed interface DetalleLibroUiState {
    data object Cargando : DetalleLibroUiState
    data class Contenido(val libro: Libro) : DetalleLibroUiState
    data object NoEncontrado : DetalleLibroUiState
}

/** Estado de la solicitud de préstamo, independiente de cómo se cargó el libro. */
sealed interface EstadoSolicitud {
    data object Inactiva : EstadoSolicitud
    data object Confirmando : EstadoSolicitud
    data object Enviando : EstadoSolicitud
    data class Exito(val mensaje: String) : EstadoSolicitud
    data class Fallida(val mensaje: String) : EstadoSolicitud
}
