package pe.upeu.biblioandes.presentation.inicio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase

/**
 * ViewModel de Inicio (RF-01). El dato del estudiante es una lectura simple
 * sin reglas de negocio, así que se toma directo del repositorio en vez de
 * envolverlo en un caso de uso adicional.
 */
class InicioViewModel(
    private val repository: BibliotecaRepository,
    private val obtenerPrestamos: ObtenerPrestamosUseCase
) : ViewModel() {

    var uiState: InicioUiState by mutableStateOf(InicioUiState.Cargando)
        private set

    init {
        cargar()
    }

    fun recargar() = cargar()

    private fun cargar() {
        uiState = InicioUiState.Cargando
        viewModelScope.launch {
            val estudiante = repository.obtenerEstudiante()
            val proximoAVencer = obtenerPrestamos()
                .filter { it.estado is EstadoPrestamo.Activo }
                .minByOrNull { it.fechaLimite }
            uiState = InicioUiState.Contenido(estudiante, proximoAVencer)
        }
    }
}
