package pe.upeu.biblioandes.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.CalcularEstadoPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.upeu.biblioandes.presentation.catalogo.CatalogoViewModel
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroViewModel
import pe.upeu.biblioandes.presentation.inicio.InicioViewModel
import pe.upeu.biblioandes.presentation.prestamos.PrestamosViewModel

val dataModule = module {
    // Se expone también el tipo concreto para que la pantalla de catálogo
    // pueda accionar la bandera de error simulado (RF-02), sin que el resto
    // de la app (casos de uso) dependa de nada distinto a la interfaz.
    single { BibliotecaRepositoryFake() }
    single<BibliotecaRepository> { get<BibliotecaRepositoryFake>() }
}

val domainModule = module {
    factory { CalcularEstadoPrestamoUseCase() }
    factory { ObtenerCatalogoUseCase(get()) }
    factory { ObtenerPrestamosUseCase(get(), get()) }
    factory { SolicitarPrestamoUseCase(get(), get()) }
    factory { DevolverPrestamoUseCase(get()) }
}

/** Se completa en cada rama de presentación con los ViewModel de esa pantalla. */
val presentationModule = module {
    viewModel { CatalogoViewModel(get(), get()) }
    viewModel { (libroId: Int) -> DetalleLibroViewModel(libroId, get(), get()) }
    viewModel { PrestamosViewModel(get(), get()) }
    viewModel { InicioViewModel(get(), get()) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, dataModule, domainModule, presentationModule)
    }
}
