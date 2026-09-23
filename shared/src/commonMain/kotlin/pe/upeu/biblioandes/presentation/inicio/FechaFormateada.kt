package pe.upeu.biblioandes.presentation.inicio

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import pe.upeu.biblioandes.domain.usecase.hoyLocal

private val DIAS = mapOf(
    DayOfWeek.MONDAY to "Lunes", DayOfWeek.TUESDAY to "Martes", DayOfWeek.WEDNESDAY to "Miércoles",
    DayOfWeek.THURSDAY to "Jueves", DayOfWeek.FRIDAY to "Viernes", DayOfWeek.SATURDAY to "Sábado",
    DayOfWeek.SUNDAY to "Domingo"
)

private val MESES = mapOf(
    Month.JANUARY to "ene", Month.FEBRUARY to "feb", Month.MARCH to "mar", Month.APRIL to "abr",
    Month.MAY to "may", Month.JUNE to "jun", Month.JULY to "jul", Month.AUGUST to "ago",
    Month.SEPTEMBER to "sep", Month.OCTOBER to "oct", Month.NOVEMBER to "nov", Month.DECEMBER to "dic"
)

/** "Miércoles, 23 sep": usado en el saludo de Inicio (RF-01). */
fun fechaDeHoyFormateada(): String {
    val hoy = hoyLocal()
    return "${DIAS[hoy.dayOfWeek]}, ${hoy.dayOfMonth} ${MESES[hoy.month]}"
}

/** "27 sep": formato corto usado en tarjetas de préstamo. */
fun fechaCortaDe(fechaIso: String): String {
    val fecha = LocalDate.parse(fechaIso)
    return "${fecha.dayOfMonth} ${MESES[fecha.month]}"
}
