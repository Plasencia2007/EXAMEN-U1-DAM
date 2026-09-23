package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/**
 * Registra la devolución de un préstamo Activo o Vencido: lo marca como
 * Devuelto y repone el ejemplar en el catálogo. Libera el cupo de RN-01 y,
 * si era el único préstamo Vencido, desbloquea RN-04 para el estudiante.
 */
class DevolverPrestamoUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(prestamoId: Int): Prestamo =
        repository.devolverPrestamo(prestamoId)
}
