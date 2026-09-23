package pe.upeu.biblioandes.data.local

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Fuente de datos en memoria (Anexo del examen). El día en que el servicio
 * web esté listo, este objeto se reemplaza por un cliente HTTP detrás de
 * BibliotecaRepositoryFake -> BibliotecaRepositoryRemoto, sin tocar dominio
 * ni presentación.
 */
object DatosSimulados {

    val estudiante = Estudiante(
        "E-2291", "Diego Huamán Ccama",
        "Ingeniería de Sistemas", "diego.huaman@correo.pe"
    )

    val categorias = listOf("Programación", "Matemática", "Redes", "Gestión", "Literatura")

    val libros = listOf(
        Libro(1, "Kotlin en profundidad", "M. Salazar",
            2023, "Programación", "Central", 3),
        Libro(2, "Estructuras de datos", "R. Peña",
            2021, "Programación", "Central", 0),
        Libro(3, "Cálculo aplicado", "L. Ortega",
            2019, "Matemática", "Sede Norte", 2),
        Libro(4, "Redes de computadoras", "A. Medina",
            2022, "Redes", "Sede Sur", 4),
        Libro(5, "Seguridad en redes", "P. Ríos",
            2024, "Redes", "Central", 0),
        Libro(6, "Gestión de proyectos", "S. Delgado",
            2021, "Gestión", "Sede Norte", 2),
        Libro(7, "Álgebra lineal", "C. Vargas",
            2020, "Matemática", "Sede Sur", 1),
        Libro(8, "Cien años de soledad", "G. García Márquez",
            1967, "Literatura", "Central", 5),
        Libro(9, "Don Quijote de la Mancha", "M. de Cervantes",
            1605, "Literatura", "Sede Norte", 2),
        Libro(10, "Programación funcional en Kotlin", "J. Reyes",
            2022, "Programación", "Sede Sur", 1),
        Libro(11, "Administración de redes Linux", "F. Quispe",
            2023, "Redes", "Sede Norte", 3),
        Libro(12, "Liderazgo y gestión de equipos", "N. Alvarado",
            2020, "Gestión", "Central", 0)
    )

    // Fechas ajustadas respecto al día de la evaluación: los dos préstamos
    // Activos vencen en el futuro; los Devueltos y el Vencido quedan en el
    // pasado. RN-03 recalcula el estado real en tiempo de lectura, así que
    // estos valores de Activo/Vencido son solo el punto de partida.
    val prestamos = listOf(
        Prestamo(1, libros[0], "2026-09-20", "2026-09-27",
            EstadoPrestamo.Activo(4)),
        Prestamo(2, libros[3], "2026-09-21", "2026-09-28",
            EstadoPrestamo.Activo(5)),
        Prestamo(3, libros[2], "2026-08-20", "2026-08-27",
            EstadoPrestamo.Devuelto("2026-08-26")),
        Prestamo(4, libros[1], "2026-08-05", "2026-08-12",
            EstadoPrestamo.Devuelto("2026-08-11")),
        Prestamo(5, libros[5], "2026-08-28", "2026-09-04",
            EstadoPrestamo.Vencido(18))
    )
}
