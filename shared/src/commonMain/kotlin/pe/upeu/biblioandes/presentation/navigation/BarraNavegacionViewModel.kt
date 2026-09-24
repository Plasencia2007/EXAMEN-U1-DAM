package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase

/**
 * SC-B: cuenta cuántos préstamos Activos tiene el estudiante para mostrar un
 * indicador en la barra de navegación inferior. Lee el conteo a través de
 * ObtenerPrestamosUseCase (dominio), igual que DetalleLibroViewModel — nunca
 * se duplica la regla RN-01 ni su umbral en la capa de presentación.
 */
class BarraNavegacionViewModel(
    private val obtenerPrestamos: ObtenerPrestamosUseCase
) : ViewModel() {

    var prestamosActivosCount: Int by mutableStateOf(0)
        private set

    init {
        recargar()
    }

    fun recargar() {
        viewModelScope.launch {
            prestamosActivosCount = obtenerPrestamos().count { it.estado is EstadoPrestamo.Activo }
        }
    }
}
