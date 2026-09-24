# BiblioAndes

Examen Parcial — Unidad 1, Versión B — Desarrollo de Aplicaciones Móviles (UPeU)
Estudiante: **Jhonsons Plasencia Valdez** (202412796) · Repositorio: `EXAMEN-U1-DAM`

Aplicación KMP (Android + iOS) para consultar el catálogo de la biblioteca
BiblioAndes y gestionar los préstamos de un estudiante. Todos los datos
provienen de una fuente simulada en memoria: no consume servicios web ni
bases de datos (Ktor, Room, SQLDelight, Retrofit, etc. están fuera de esta
versión, tal como exige el enunciado).

## Cómo ejecutar

- **Android**: abrir el proyecto en Android Studio y correr la configuración
  `androidApp`, o desde consola: `./gradlew :androidApp:installDebug`.
- **iOS**: abrir `iosApp/iosApp.xcodeproj` en Xcode y ejecutar sobre un
  simulador. También se puede verificar solo la compilación del módulo común
  con `./gradlew :shared:compileKotlinIosSimulatorArm64`.

## Arquitectura

Clean Architecture + MVVM, con el dominio y la mayor parte de la
presentación viviendo en `commonMain` (compartido entre Android e iOS):

```
shared/src/commonMain/kotlin/pe/upeu/biblioandes/
├── domain/
│   ├── model/          Libro, Prestamo, EstadoPrestamo (sealed class), Estudiante
│   ├── repository/     BibliotecaRepository (solo la interfaz)
│   └── usecase/        ObtenerCatalogoUseCase, SolicitarPrestamoUseCase,
│                        ObtenerPrestamosUseCase, CalcularEstadoPrestamoUseCase
├── data/
│   ├── local/           DatosSimulados (estudiante, categorías, 12 libros, 5 préstamos)
│   └── repository/       BibliotecaRepositoryFake (retardo simulado de 800 ms,
│                          bandera de error solo para el catálogo)
├── presentation/
│   ├── inicio/, catalogo/, detalle/, prestamos/, perfil/   ViewModel + UiState + Screen
│   ├── navigation/       Screen (rutas tipadas), BackStack propio, AppNavHost, BackHandler
│   └── theme/            Color, Type, BiblioAndesTheme (Material 3, claro/oscuro)
└── di/                   AppModule (Koin: dataModule, domainModule, presentationModule)
```

**Reglas de negocio** (RN-01 a RN-04) viven únicamente en `domain/usecase`,
nunca en un composable:

- RN-01: máximo 3 préstamos Activos simultáneos (`SolicitarPrestamoUseCase`).
- RN-02: no se puede solicitar un libro con 0 ejemplares disponibles.
- RN-03: todo préstamo dura 7 días; el estado se recalcula contra la fecha
  actual en `CalcularEstadoPrestamoUseCase` (Activo/Vencido).
- RN-04: un estudiante con un préstamo Vencido no puede solicitar otro libro.

**Decisiones técnicas destacadas**:

- Navegación con una pila propia (`BackStack`) en vez de una librería externa,
  para cubrir literalmente `AppNavHost.kt` / `Destinos.kt` sin depender de
  Navigation Compose. El botón atrás del sistema se resuelve con un
  `BackHandler` `expect`/`actual` (real en Android vía `activity-compose`,
  no-op en iOS).
- Cada pantalla con datos tiene sus 3–4 estados de interfaz (carga, contenido,
  vacío y, solo en catálogo, error) modelados como `sealed interface` en su
  propio `UiState`.
- El estado de error del catálogo se simula con una bandera en
  `BibliotecaRepositoryFake`, expuesta a Koin además de la interfaz de
  dominio, para que únicamente la pantalla de catálogo pueda accionarla.
- Tema Material 3 propio (paleta azul marino editorial + crema cálido con
  acento cobre) calcado del prototipo de diseño entregado, con tokens
  semánticos propios (`BiblioAndesColors`: hero, feature, track, chev,
  badLine, etc.) porque Material 3 no tiene roles para todos ellos. Modo
  claro/oscuro conmutable desde el botón de cada cabecera y desde Perfil
  (RF-06), aplicado de inmediato a toda la app.
- Cada pantalla dibuja su propia cabecera (eyebrow + título + botón de
  tema) en vez de una TopAppBar compartida, para replicar fielmente el
  prototipo. Componentes de "lomo de libro" (`LomoMonograma`/`LomoPortada`)
  con color y monograma deterministicos por libro.

## Solicitud de cambio (Parte II) — SC-A

Rama `sc-a-plasencia`, creada desde `develop`. Agrega un chip "Solo
disponibles" al catálogo (icono de filtros en la cabecera), que oculta los
libros con `ejemplaresDisponibles == 0` y se combina con el filtro de
categoría y la búsqueda ya existentes. El filtro se resuelve por completo en
`CatalogoViewModel.publicarContenidoFiltrado()`; ningún composable decide
qué libro se muestra. Verificado en el emulador: 12 → 9 títulos al activarlo,
los 3 libros sin stock desaparecen y el contador indica "solo disponibles".

## Flujo de Git

`main` solo recibe fusiones de `develop`. Cada funcionalidad se desarrolló en
una rama `feature/<funcionalidad>-plasencia`, con commits pequeños en español
(prefijos `feat`, `fix`, `refactor`, `docs`) y fusión `--no-ff` hacia
`develop`. Historial completo: `git log --graph --oneline --all`.
