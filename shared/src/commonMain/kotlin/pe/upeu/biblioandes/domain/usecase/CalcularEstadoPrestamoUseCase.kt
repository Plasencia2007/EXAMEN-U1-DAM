package pe.upeu.biblioandes.domain.usecase

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * RN-03: todo préstamo dura siete días; si la fecha límite ya pasó, el
 * préstamo se recalcula como Vencido. Vive en el dominio porque tanto
 * [ObtenerPrestamosUseCase] como [SolicitarPrestamoUseCase] necesitan saber
 * el estado *actual* de un préstamo, no el que quedó grabado en el momento
 * en que se creó.
 *
 * Un préstamo Devuelto no se recalcula: su estado ya es definitivo.
 */
class CalcularEstadoPrestamoUseCase {

    operator fun invoke(prestamo: Prestamo, hoy: LocalDate = hoyLocal()): Prestamo {
        if (prestamo.estado is EstadoPrestamo.Devuelto) return prestamo

        val fechaLimite = LocalDate.parse(prestamo.fechaLimite)
        val diasParaVencer = hoy.daysUntil(fechaLimite)

        val nuevoEstado = if (diasParaVencer >= 0) {
            EstadoPrestamo.Activo(diasRestantes = diasParaVencer)
        } else {
            EstadoPrestamo.Vencido(diasDeAtraso = -diasParaVencer)
        }

        return prestamo.copy(estado = nuevoEstado)
    }
}

internal fun hoyLocal(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
