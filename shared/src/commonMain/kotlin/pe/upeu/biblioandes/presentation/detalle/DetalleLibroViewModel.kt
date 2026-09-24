package pe.upeu.biblioandes.presentation.detalle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.usecase.LIMITE_PRESTAMOS_ACTIVOS
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase

/**
 * ViewModel del detalle de un libro (RF-03). Reutiliza [ObtenerCatalogoUseCase]
 * para ubicar el libro por id en vez de exponer un método nuevo en el
 * repositorio: el catálogo completo ya vive en memoria y es una sola
 * llamada barata con el retardo simulado.
 *
 * SC-B: también usa [ObtenerPrestamosUseCase] para saber si el estudiante ya
 * alcanzó LIMITE_PRESTAMOS_ACTIVOS y deshabilitar el botón de forma
 * proactiva, leyendo el límite desde el dominio (misma constante que aplica
 * SolicitarPrestamoUseCase) en vez de duplicar el número "3" en la UI.
 */
class DetalleLibroViewModel(
    private val libroId: Int,
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val solicitarPrestamo: SolicitarPrestamoUseCase
) : ViewModel() {

    var uiState: DetalleLibroUiState by mutableStateOf(DetalleLibroUiState.Cargando)
        private set

    var estadoSolicitud: EstadoSolicitud by mutableStateOf(EstadoSolicitud.Inactiva)
        private set

    init {
        cargar()
    }

    private fun cargar() {
        uiState = DetalleLibroUiState.Cargando
        viewModelScope.launch {
            val libro = obtenerCatalogo().find { it.id == libroId }
            uiState = if (libro != null) {
                val prestamosActivos = obtenerPrestamos().count { it.estado is EstadoPrestamo.Activo }
                DetalleLibroUiState.Contenido(
                    libro = libro,
                    limiteAlcanzado = prestamosActivos >= LIMITE_PRESTAMOS_ACTIVOS
                )
            } else {
                DetalleLibroUiState.NoEncontrado
            }
        }
    }

    fun pedirConfirmacion() {
        estadoSolicitud = EstadoSolicitud.Confirmando
    }

    fun cancelarConfirmacion() {
        estadoSolicitud = EstadoSolicitud.Inactiva
    }

    fun confirmarSolicitud() {
        val libro = (uiState as? DetalleLibroUiState.Contenido)?.libro ?: return
        estadoSolicitud = EstadoSolicitud.Enviando
        viewModelScope.launch {
            solicitarPrestamo(libro)
                .onSuccess {
                    estadoSolicitud = EstadoSolicitud.Exito("Préstamo registrado. Fecha límite: ${it.fechaLimite}")
                    cargar()
                }
                .onFailure { error ->
                    estadoSolicitud = EstadoSolicitud.Fallida(error.message ?: "No se pudo registrar el préstamo")
                }
        }
    }
}
