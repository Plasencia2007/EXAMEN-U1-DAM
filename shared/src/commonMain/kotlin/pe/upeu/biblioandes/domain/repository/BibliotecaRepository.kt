package pe.upeu.biblioandes.domain.repository

import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Contrato de la biblioteca: qué se puede consultar del catálogo y de los
 * préstamos, y cómo se registra uno nuevo. No sabe nada de Compose, Koin,
 * ni de si los datos vienen de memoria o de un servicio web.
 */
interface BibliotecaRepository {
    suspend fun obtenerEstudiante(): Estudiante
    suspend fun listarCatalogo(): List<Libro>
    suspend fun listarPrestamos(): List<Prestamo>
    suspend fun registrarPrestamo(prestamo: Prestamo): Prestamo
}
