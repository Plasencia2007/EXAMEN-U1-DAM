package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * Devuelve los préstamos del estudiante con su estado recalculado (RN-03)
 * y ordenados por fecha de devolución más próxima (RF-04).
 */
class ObtenerPrestamosUseCase(
    private val repository: BibliotecaRepository,
    private val calcularEstadoPrestamo: CalcularEstadoPrestamoUseCase
) {
    suspend operator fun invoke(): List<Prestamo> {
        return repository.listarPrestamos()
            .map { calcularEstadoPrestamo(it) }
            .sortedBy { it.fechaLimite }
    }
}
