package pe.upeu.biblioandes.domain.model

data class Libro(
    val id: Int,
    val titulo: String,
    val autor: String,
    val anio: Int,
    val categoria: String,
    val sede: String,
    val ejemplaresDisponibles: Int
) {
    /** RN-02 (lectura): un libro sin ejemplares no puede solicitarse. */
    val estaDisponible: Boolean
        get() = ejemplaresDisponibles > 0
}
