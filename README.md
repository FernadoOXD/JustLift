# JustLift 

Aplicación Android nativa para gestionar **rutinas de gimnasio**. Cada usuario se registra, inicia sesión y administra sus propias rutinas de entrenamiento (crear, ver, editar y eliminar).

Proyecto Integrador de la **Unidad 3** · Desarrollo de aplicaciones móviles · Android nativo · Kotlin.

**Autor:** Fernando Reyes Gómez · **Institución:** Universidad Politécnica de Chiapas (UPChiapas) · **Dominio:** Gimnasio · **Recurso principal:** Rutina

> **Repositorio del Backend:** [JustLift_Backend](https://github.com/FernadoOXD/JustLift_Backend.git)  
> **API REST en la Nube (Producción):** `https://justlift-backend.onrender.com/`

---

## Contenido

- [Funcionalidades](#funcionalidades)
- [Capturas](#capturas)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Modelo de datos](#modelo-de-datos)
- [Navegación](#navegación)
- [Cómo ejecutar](#cómo-ejecutar)
- [Solución de problemas](#solución-de-problemas)
- [Decisiones de diseño](#decisiones-de-diseño)
- [Limitaciones conocidas](#limitaciones-conocidas)
- [Autor](#autor)

---

## Funcionalidades

- **Registro** con nombre, correo, contraseña y confirmación (validaciones en el `ViewModel`).
- **Inicio de sesión** con mensajes claros ante credenciales inválidas o falta de conexión.
- **Sesión persistente:** al abrir la app con sesión activa en `AuthPreferences`, navega directamente a Home.
- **Cierre de sesión** que borra el token JWT localmente y limpia la pila de navegación (el botón Atrás no regresa).
- **CRUD de rutinas** (cada usuario solo gestiona las suyas):
  - **Listar** con `LazyColumn`, manejando estados de UI (**carga**, **vacío** y **error con reintento**).
  - **Ver detalle** completo de una rutina.
  - **Crear y editar** con un formulario reutilizable y validado.
  - **Eliminar** con diálogo de confirmación (`AlertDialog`).
- **Retroalimentación** al usuario en cada operación (mensajes informativos, errores y navegación).

---

## Capturas

<!-- Puedes adjuntar capturas de tu app aquí:
| Login | Home (Rutinas) | Formulario |
|---|---|---|
| ![Login](docs/login.png) | ![Home](docs/home.png) | ![Form](docs/form.png) |
-->

---

## Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material 3 + Material Icons Extended |
| Navegación | Navigation Compose |
| Estado | `ViewModel` + `StateFlow` (`UiState` inmutable) con `collectAsStateWithLifecycle()` |
| Asincronía | Corrutinas (`viewModelScope`, funciones `suspend`) |
| Red | Retrofit 3.0.0 + `converter-kotlinx-serialization` + OkHttp 4.12.0 (con `AuthInterceptor`) |
| Sesión | `SharedPreferences` encapsulado tras la interfaz `AuthPreferences` |
| Inyección de dependencias | Manual: `AppContainer` + `ViewModelProvider.Factory` (sin Hilt, Dagger ni Koin) |
| Dependencias | Version Catalog (`gradle/libs.versions.toml`) |

---

## Arquitectura

**MVVM + Clean Architecture** estructurada en capas desacopladas con patrones Repository, DTO y Mapper.

```text
 UI (Compose)  ──eventos──▶  ViewModel  ──▶  Repository (interfaz, domain)
      ▲                         │                       │
      └────── UiState ◀─────────┘                       ▼
                                              RepositoryImpl (data)
                                                │          │
                                              Mapper    ApiService (Retrofit)
                                         (DTO ↔ dominio)       │
                                                          API REST
```

- **Composables (UI):** No contienen lógica de negocio; observan el `StateFlow` e invocan lambdas de evento hacia el `ViewModel`.
- **ViewModels:** Reciben dependencias por constructor, exponen un `UiState` inmutable y mantienen su `MutableStateFlow` privado. El estado sobrevive a la rotación de pantalla.
- **Domain:** Interfaces de repositorio y modelos de dominio puros (ej. `Rutina`, `User`).
- **Data (DTO y Mapper):** Objetos DTO (`@Serializable`) aislados dentro de la capa de datos. Los mappers (`RutinaMapper`) traducen DTO ↔ dominio.
- **AppContainer:** Instanciado una sola vez en `KtApplication`. Construye el cliente OkHttp, Retrofit, `AuthPreferences` y los repositorios concretos.
- **AuthInterceptor:** Inyecta automáticamente la cabecera `Authorization: Bearer <token>` en cada petición HTTP protegida.

---

## Estructura del proyecto

```text
app/src/main/java/com/upchiapas/kt_template/
├── KtApplication.kt              # Inicializa el AppContainer global
├── MainActivity.kt               # NavHost y verificación inicial de token
├── core/
│   ├── di/
│   │   └── AppContainer.kt       # Contenedor de inyección de dependencias manual
│   ├── network/
│   │   └── AuthInterceptor.kt    # Interceptor OkHttp para el Bearer Token
│   └── storage/
│       ├── AuthPreferences.kt    # Interfaz de sesión
│       └── AuthPreferencesImpl.kt# Implementación con SharedPreferences
├── auth/
│   ├── data/                     # AuthApiService, AuthDtos, AuthRepositoryImpl
│   ├── domain/                   # User, AuthRepository, AuthValidator
│   └── presentation/             # Login (Screen/VM/State) y Register (Screen/VM/State)
└── gym/
    ├── data/                     # RutinasApiService, RutinaDto, RutinaMapper, RutinasRepositoryImpl
    ├── domain/                   # Rutina, RutinasRepository, RutinaValidator
    └── presentation/             # Home, Detail y Form (Screens/VMs/States)
```

---

## Modelo de datos

### Rutina (`Rutina`)

| Atributo | Tipo | Validación / Regla |
|---|---|---|
| `id` | `Int` | Asignado automáticamente por la API REST |
| `nombre` | `String` | Obligatorio, mínimo 3 caracteres |
| `grupoMuscular` | `String` | Obligatorio (ej. "Pecho", "Espalda", "Pierna") |
| `series` | `Int` | Numérico obligatorio, entre 1 y 20 |
| `repeticiones` | `Int` | Numérico obligatorio, entre 1 y 100 |
| `activa` | `Boolean` | Booleano obligatorio (`true` / `false`) |

Cada rutina se vincula en la capa de datos con el campo `userId` devuelto por `json-server-auth`.

### Registro e Inicio de Sesión de Usuario

- **Nombre:** Obligatorio.
- **Correo:** Formato de e-mail válido.
- **Contraseña:** Mínimo 8 caracteres.
- **Confirmación:** Debe coincidir exactamente con la contraseña.

---

## Navegación

| Ruta | Pantalla | Descripción |
|---|---|---|
| `login` | `LoginScreen` | Formulario de autenticación |
| `register` | `RegisterScreen` | Formulario de registro de cuenta |
| `home` | `HomeScreen` | Lista de rutinas con acciones CRUD y cerrar sesión |
| `detail/{rutinaId}` | `RutinaDetailScreen` | Vista detallada de una rutina |
| `form?rutinaId={rutinaId}` | `RutinaFormScreen` | Reutilizable: sin ID crea rutina nueva, con ID edita |

---

## Cómo ejecutar

La aplicación se puede conectar a la **API en la nube (Render)** o a un **servidor local (Node.js)**.

### Opción A: Probar con la API en la Nube (Producción)

No requiere ejecutar backend local. Solo abre la app en Android Studio con la URL pública configurada en `AppContainer.kt`:

```kotlin
.baseUrl("https://justlift-backend.onrender.com/")
```

> **Nota:** Al usar el plan gratuito de Render, la primera petición tras 15 minutos de inactividad puede tardar unos 30 segundos en responder mientras reactiva el contenedor.

---

### Opción B: Ejecutar con Backend Local (`json-server-auth`)

#### 1. Clonar e iniciar el backend local

```bash
git clone https://github.com/FernadoOXD/JustLift_Backend.git
cd JustLift_Backend
npm install
npm start
```

El script inicia `json-server-auth db.json --host 0.0.0.0 --port 3000 --routes routes.json`.

#### 2. Configurar el túnel ADB (si usas dispositivo físico o emulador por USB)

Asegúrate de que la app apunte a `http://localhost:3000/` en `AppContainer.kt` y ejecuta en PowerShell:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:3000 tcp:3000
```

#### 3. Compilar y ejecutar la App

1. Abre el proyecto en Android Studio.
2. Selecciona la variante de compilación **debug** (*Build → Select Build Variant*).
3. Ejecuta con **Run ▶** (`Shift + F10`).

---

## Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `SocketTimeoutException` en teléfono físico | Usar `10.0.2.2` fuera del emulador virtual | Configurar la app con `http://localhost:3000/` y ejecutar `adb reverse tcp:3000 tcp:3000` |
| `Unresolved reference 'Icons'` | Falta la dependencia de íconos extendidos | Añadir `implementation("androidx.compose.material:material-icons-extended")` en `build.gradle.kts` |
| `Unresolved reference 'authRepository'` | Propiedad no expuesta en `AppContainer` | Declarar `val authRepository: AuthRepository by lazy { ... }` en `AppContainer.kt` |
| La API en Render no responde al inicio | Servidor en reposo (Free Tier) | Espera 30 segundos en la primera petición para que Render despierte el servicio |

---

## Decisiones de diseño

- **Un solo `AppContainer`:** Centraliza la inyección de dependencias manual garantizando que `Retrofit`, `OkHttpClient` y `AuthPreferences` existan como instancias únicas compartidas (`@Singleton` implícito).
- **Abstracción de `AuthPreferences`:** Permite consultar la presencia del token al arrancar `MainActivity`, omitiendo la pantalla de Login si el usuario ya inició sesión.
- **Formulario Reutilizable (`RutinaFormScreen`):** Comparte la misma UI y `ViewModel` para creación y edición, poblando el estado si se recibe un `rutinaId`.
- **Validaciones Puras (`AuthValidator`, `RutinaValidator`):** Clases independientes sin dependencias del SDK de Android, facilitando las pruebas unitarias.

---

## Limitaciones conocidas

- En la versión simulada local con `json-server-auth`, los tokens expiran en **1 hora**.
- Las instancias gratuitas de Render se desactivan por inactividad tras 15 minutos sin peticiones HTTP.

---

## Autor

- **Nombre:** Fernando Reyes Gómez
- **GitHub:** [@FernadoOXD](https://github.com/FernadoOXD)
- **Institución:** Universidad Politécnica de Chiapas (UPChiapas)
