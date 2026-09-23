package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

class ObtenerCatalogoUseCase(
    private val repository: BibliotecaRepository
) {
    suspend operator fun invoke(): List<Libro> = repository.listarCatalogo()
}
