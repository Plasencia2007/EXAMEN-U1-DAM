package pe.upeu.biblioandes.domain.usecase

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

const val LIMITE_PRESTAMOS_ACTIVOS = 3
const val DURACION_PRESTAMO_DIAS = 7

/** El libro solicitado no tiene ejemplares disponibles (RN-02). */
class LibroNoDisponibleException :
    Exception("No hay ejemplares disponibles de este libro")

/** El estudiante tiene un préstamo Vencido y no puede pedir uno nuevo (RN-04). */
class PrestamoVencidoPendienteException :
    Exception("Tienes un préstamo vencido; regulariza tu situación antes de solicitar otro libro")

/** El estudiante ya tiene el máximo de préstamos Activos permitidos (RN-01). */
class LimitePrestamosActivosException :
    Exception("Ya tienes $LIMITE_PRESTAMOS_ACTIVOS préstamos activos; no puedes solicitar otro")

/**
 * Aplica RN-01, RN-02 y RN-04 antes de registrar un préstamo nuevo.
 * El id definitivo y el descuento del ejemplar los resuelve el repositorio,
 * no este caso de uso ni la pantalla.
 */
class SolicitarPrestamoUseCase(
    private val repository: BibliotecaRepository,
    private val calcularEstadoPrestamo: CalcularEstadoPrestamoUseCase
) {
    suspend operator fun invoke(libro: Libro): Result<Prestamo> {
        if (!libro.estaDisponible) {
            return Result.failure(LibroNoDisponibleException())
        }

        val prestamosActuales = repository.listarPrestamos().map { calcularEstadoPrestamo(it) }

        val tienePrestamoVencido = prestamosActuales.any { it.estado is EstadoPrestamo.Vencido }
        if (tienePrestamoVencido) {
            return Result.failure(PrestamoVencidoPendienteException())
        }

        val prestamosActivos = prestamosActuales.count { it.estado is EstadoPrestamo.Activo }
        if (prestamosActivos >= LIMITE_PRESTAMOS_ACTIVOS) {
            return Result.failure(LimitePrestamosActivosException())
        }

        val hoy = hoyLocal()
        val fechaLimite = hoy.plus(DURACION_PRESTAMO_DIAS, DateTimeUnit.DAY)
        val prestamoNuevo = Prestamo(
            id = 0,
            libro = libro,
            fechaPrestamo = hoy.toString(),
            fechaLimite = fechaLimite.toString(),
            estado = EstadoPrestamo.Activo(diasRestantes = DURACION_PRESTAMO_DIAS)
        )

        return Result.success(repository.registrarPrestamo(prestamoNuevo))
    }
}
