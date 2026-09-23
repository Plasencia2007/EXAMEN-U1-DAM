package pe.upeu.biblioandes.presentation.prestamos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase

/** ViewModel de "Mis préstamos" (RF-04): orden por devolución más próxima, filtro por estado y devolución manual. */
class PrestamosViewModel(
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val devolverPrestamo: DevolverPrestamoUseCase
) : ViewModel() {

    private var todosLosPrestamos: List<Prestamo> = emptyList()
    private var filtroSeleccionado: FiltroEstado = FiltroEstado.TODOS

    var uiState: PrestamosUiState by mutableStateOf(PrestamosUiState.Cargando)
        private set

    /** Id del préstamo que se está devolviendo, para deshabilitar su botón mientras dura la operación. */
    var prestamoIdEnProceso: Int? by mutableStateOf(null)
        private set

    init {
        cargar()
    }

    fun recargar() = cargar()

    fun seleccionarFiltro(filtro: FiltroEstado) {
        filtroSeleccionado = filtro
        publicarContenidoFiltrado()
    }

    fun devolver(prestamoId: Int) {
        if (prestamoIdEnProceso != null) return
        prestamoIdEnProceso = prestamoId
        viewModelScope.launch {
            devolverPrestamo(prestamoId)
            prestamoIdEnProceso = null
            cargar()
        }
    }

    private fun cargar() {
        uiState = PrestamosUiState.Cargando
        viewModelScope.launch {
            todosLosPrestamos = obtenerPrestamos()
            publicarContenidoFiltrado()
        }
    }

    private fun publicarContenidoFiltrado() {
        val prestamosFiltrados = todosLosPrestamos.filter { prestamo ->
            when (filtroSeleccionado) {
                FiltroEstado.TODOS -> true
                FiltroEstado.ACTIVO -> prestamo.estado is EstadoPrestamo.Activo
                FiltroEstado.DEVUELTO -> prestamo.estado is EstadoPrestamo.Devuelto
                FiltroEstado.VENCIDO -> prestamo.estado is EstadoPrestamo.Vencido
            }
        }
        uiState = PrestamosUiState.Contenido(
            filtroSeleccionado = filtroSeleccionado,
            prestamos = prestamosFiltrados
        )
    }
}
