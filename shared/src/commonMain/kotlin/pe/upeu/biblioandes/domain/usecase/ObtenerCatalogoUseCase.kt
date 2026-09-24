package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.OrdenCatalogo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

class ObtenerCatalogoUseCase(
    private val repository: BibliotecaRepository
) {
    /** SC-C: el ordenamiento es una operación del dominio, no de la pantalla. */
    suspend operator fun invoke(orden: OrdenCatalogo = OrdenCatalogo.TITULO): List<Libro> {
        val libros = repository.listarCatalogo()
        return when (orden) {
            OrdenCatalogo.TITULO -> libros.sortedBy { it.titulo }
            OrdenCatalogo.ANIO -> libros.sortedBy { it.anio }
        }
    }
}
