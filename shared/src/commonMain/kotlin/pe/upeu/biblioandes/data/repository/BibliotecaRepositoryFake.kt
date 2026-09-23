package pe.upeu.biblioandes.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.biblioandes.data.local.DatosSimulados
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.hoyLocal

/**
 * Implementación simulada de [BibliotecaRepository]: datos en memoria con un
 * retardo de 800 ms por operación (para que el estado de carga sea visible)
 * y una bandera para forzar el estado de error del catálogo desde fuera,
 * sin tocar la interfaz de dominio.
 */
class BibliotecaRepositoryFake : BibliotecaRepository {

    private val mutex = Mutex()
    private val libros = DatosSimulados.libros.toMutableList()
    private val prestamos = DatosSimulados.prestamos.toMutableList()
    private var siguienteId = (prestamos.maxOfOrNull { it.id } ?: 0) + 1

    /** Bandera de evidencia: actívala para capturar el estado de error del catálogo (RF-02). */
    var forzarErrorCatalogo: Boolean = false

    override suspend fun obtenerEstudiante(): Estudiante {
        delay(RETARDO_MS)
        return DatosSimulados.estudiante
    }

    override suspend fun listarCatalogo(): List<Libro> {
        delay(RETARDO_MS)
        if (forzarErrorCatalogo) {
            error("No se pudo cargar el catálogo de BiblioAndes")
        }
        return mutex.withLock { libros.toList() }
    }

    override suspend fun listarPrestamos(): List<Prestamo> {
        delay(RETARDO_MS)
        return mutex.withLock { prestamos.toList() }
    }

    override suspend fun registrarPrestamo(prestamo: Prestamo): Prestamo {
        delay(RETARDO_MS)
        return mutex.withLock {
            val indiceLibro = libros.indexOfFirst { it.id == prestamo.libro.id }
            val libroActualizado = libros[indiceLibro].let { libro ->
                libro.copy(ejemplaresDisponibles = libro.ejemplaresDisponibles - 1)
            }
            libros[indiceLibro] = libroActualizado

            val prestamoConId = prestamo.copy(id = siguienteId, libro = libroActualizado)
            siguienteId += 1
            prestamos.add(prestamoConId)
            prestamoConId
        }
    }

    override suspend fun devolverPrestamo(prestamoId: Int): Prestamo {
        delay(RETARDO_MS)
        return mutex.withLock {
            val indicePrestamo = prestamos.indexOfFirst { it.id == prestamoId }
            require(indicePrestamo >= 0) { "No existe el préstamo $prestamoId" }

            val prestamoDevuelto = prestamos[indicePrestamo].copy(
                estado = EstadoPrestamo.Devuelto(fechaDevolucion = hoyLocal().toString())
            )
            prestamos[indicePrestamo] = prestamoDevuelto

            val indiceLibro = libros.indexOfFirst { it.id == prestamoDevuelto.libro.id }
            if (indiceLibro >= 0) {
                libros[indiceLibro] = libros[indiceLibro].let { libro ->
                    libro.copy(ejemplaresDisponibles = libro.ejemplaresDisponibles + 1)
                }
            }

            prestamoDevuelto
        }
    }

    private companion object {
        const val RETARDO_MS = 800L
    }
}
