package pe.upeu.biblioandes.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.data.local.DatosSimulados
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * ViewModel del catálogo (RF-02) con filtro por categoría (chips) y búsqueda
 * por título/autor sin distinguir mayúsculas ni tildes (RF-05). Guarda todos
 * los libros obtenidos y filtra en memoria: no repite la llamada al
 * caso de uso por cada tecleo.
 */
class CatalogoViewModel(
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val repositorioFake: BibliotecaRepositoryFake
) : ViewModel() {

    private var todosLosLibros: List<Libro> = emptyList()
    private var categoriaSeleccionada: String? = null
    private var textoBusqueda: String = ""
    private var soloDisponibles: Boolean = false

    var uiState: CatalogoUiState by mutableStateOf(CatalogoUiState.Cargando)
        private set

    init {
        cargar()
    }

    fun reintentar() {
        repositorioFake.forzarErrorCatalogo = false
        cargar()
    }

    /** Solo para evidencia del estado de error (RF-02): activa la bandera del repositorio fake y recarga. */
    fun forzarErrorYRecargar() {
        repositorioFake.forzarErrorCatalogo = true
        cargar()
    }

    fun seleccionarCategoria(categoria: String?) {
        categoriaSeleccionada = categoria
        publicarContenidoFiltrado()
    }

    fun actualizarBusqueda(texto: String) {
        textoBusqueda = texto
        publicarContenidoFiltrado()
    }

    /** SC-A: alterna el filtro "Solo disponibles", combinable con categoría y búsqueda. */
    fun alternarSoloDisponibles() {
        soloDisponibles = !soloDisponibles
        publicarContenidoFiltrado()
    }

    private fun cargar() {
        uiState = CatalogoUiState.Cargando
        viewModelScope.launch {
            runCatching { obtenerCatalogo() }
                .onSuccess { libros ->
                    repositorioFake.forzarErrorCatalogo = false
                    todosLosLibros = libros
                    publicarContenidoFiltrado()
                }
                .onFailure { error ->
                    uiState = CatalogoUiState.Error(error.message ?: "No se pudo cargar el catálogo")
                }
        }
    }

    private fun publicarContenidoFiltrado() {
        val filtroTexto = normalizarTexto(textoBusqueda)
        val librosFiltrados = todosLosLibros
            .filter { categoriaSeleccionada == null || it.categoria == categoriaSeleccionada }
            .filter { !soloDisponibles || it.estaDisponible }
            .filter {
                filtroTexto.isBlank() ||
                    normalizarTexto(it.titulo).contains(filtroTexto) ||
                    normalizarTexto(it.autor).contains(filtroTexto)
            }
        uiState = CatalogoUiState.Contenido(
            categorias = DatosSimulados.categorias,
            categoriaSeleccionada = categoriaSeleccionada,
            textoBusqueda = textoBusqueda,
            libros = librosFiltrados,
            totalLibros = todosLosLibros.size,
            conteoPorCategoria = todosLosLibros.groupingBy { it.categoria }.eachCount(),
            soloDisponibles = soloDisponibles
        )
    }
}
