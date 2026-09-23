package pe.upeu.biblioandes.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.CalcularEstadoPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase

val dataModule = module {
    single<BibliotecaRepository> { BibliotecaRepositoryFake() }
}

val domainModule = module {
    factory { CalcularEstadoPrestamoUseCase() }
    factory { ObtenerCatalogoUseCase(get()) }
    factory { ObtenerPrestamosUseCase(get(), get()) }
    factory { SolicitarPrestamoUseCase(get(), get()) }
}

/** Se completa en cada rama de presentación con los ViewModel de esa pantalla. */
val presentationModule = module {}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, dataModule, domainModule, presentationModule)
    }
}
