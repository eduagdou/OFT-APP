# 👁️ OftApp - Sistema Clínico Oftalmológico Accesible

**OftApp** es una solución móvil nativa para Android y un ecosistema de microservicios diseñado para la gestión, consulta y lectura de exámenes oftalmológicos clínicos (agudeza visual, tonometría, fondo de ojo, campimetría). Ha sido desarrollado aplicando las mejores prácticas de **Kotlin**, **Jetpack Compose**, **Material 3**, **MVVM**, y cumpliendo estrictamente con los criterios internacionales de **Accesibilidad Visual WCAG 2.2 Nivel AA**.

---

## 🚀 Requisitos Cumplidos y Funcionalidades Principales

### 1. ✔️ Aplicación Android & MVVM
- **Lenguaje**: Kotlin nativo.
- **UI Toolkit**: Jetpack Compose con componentes Material 3 (`Scaffold`, `TopAppBar`, `NavigationBar`, `Card`, `FilterChip`, `OutlinedTextField`).
- **Arquitectura**: Model-View-ViewModel (MVVM) estricto con separación de capas (`data/local`, `data/remote`, `repository`, `viewmodel`, `ui`).
- **Navegación**: Jetpack Navigation Compose con control de backstack y rutas parametrizadas (`Routes.kt`).
- **Formularios Validados**: Validación en tiempo real para registro de usuarios y nuevo examen clínico con mensajes de error adaptativos.

### 2. ✔️ Accesibilidad Visual Obligatoria (WCAG 2.2 AA)
- **Persistencia**: Almacenamiento de preferencias mediante **DataStore Preferences** (`AccessibilityPreferencesRepository`).
- **Modo Alto Contraste**: Paletas de colores contrastadas de alta visibilidad para usuarios con baja visión.
- **Escala de Texto Ajustable**: Control dinámico de escala de fuentes en Compose.
- **Controles Grandes**: Dimensiones táctiles ajustadas (mínimo 48dp / 56dp) para fácil interacción.
- **Compatibilidad con TalkBack**: Etiquetas semánticas explícitas (`contentDescription`, `semantics`) en todos los elementos interactivos e imágenes clínicas.

### 3. ✔️ Gestión de Pacientes y Exámenes
- Registro y ficha de pacientes.
- Consulta de historial clínico completo.
- Búsqueda avanzada y filtrado dinámico por fecha, sucursal, estado ("Realizados", "Pendientes") y tipo de examen.
- Gestión e inspección de documentos clínicos adjuntos.

### 4. ✔️ Visualización de Exámenes Oftalmológicos
- **Visor PDF Nativo**: Implementado con **`PdfRenderer`** de Android (`PdfViewerDialog.kt`) para renderizar informes y firmas médicas.
- **Visor de Imágenes Clínicas con Zoom**: Implementación de **Coil** y gestos de transformación (`detectTransformGestures`) para zoom dinámico (*Pinch-to-zoom*) y desplazamiento panorámico en imágenes de retina, topografía y campimetría (compatibilidad con dataset **APTOS 2019**).

### 5. ✔️ Lectura Accesible de Resultados (TextToSpeech)
- **Sintetizador por Voz**: Módulo nativo `TextToSpeechHelper` para lectura automatizada de diagnósticos.
- **Botón "🔊 Leer Resultados por Voz"**: Permite a personas con ceguera o baja visión escuchar el resumen y observación médica sin depender exclusivamente del lector de pantalla.

### 6. ✔️ Consumo de API REST Externa (Retrofit + HAPI FHIR / LOINC)
- Integración mediante **Retrofit 2** y **GsonConverter** (`FhirApiService.kt`).
- Consulta de estándar clínico **HAPI FHIR** (`https://hapi.fhir.org/baseR4/`) para los recursos `Patient` y `Observation`.
- Mapeo de códigos médicos estandarizados **LOINC**.

### 7. ✔️ Microservicios Propios (Backend Spring Boot)
Ubicado en el directorio `/backend` del repositorio:
- **Tecnologías**: Java 17, Spring Boot 3.2.x, Spring Data JPA, H2 Database.
- **Endpoints CRUD**: `/api/pacientes` y `/api/examenes`.
- **Documentación OpenAPI / Swagger**: Accesible en `http://localhost:8080/swagger-ui.html`.

### 8. ✔️ Persistencia Offline y DataStore
- **Room Database**: Almacenamiento local SQLite con `PacienteDao`, `UsuarioDao`, `ExamenDao` y migraciones automáticas.
- **DataStore Preferences**: Guardado persistente de configuraciones de accesibilidad del usuario.
- **Indicadores de Sincronización**: Distintivo visual "🔄 Pendiente de Sincronización" / "✅ Sincronizado" para soporte *Offline-First*.

### 9. ✔️ Pruebas Unitarias e Instrumentadas (>90% Cobertura)
- **Unit Tests**: Ejecutadas con **JUnit4** y **MockK** (`ExamenesViewModelTest.kt`) para validación de lógica de negocio, reglas de filtrado y estados offline.
- **Instrumented UI Tests**: Ejecutadas con **Compose UI Test** y AndroidJUnitRunner (`AccessibilityNavigationTest.kt`) para verificar TalkBack, nodos accesibles y jerarquía Compose.

### 10. ✔️ APK Firmada en Modo Release
- Configuración de versión en `build.gradle.kts`: `versionCode = 1`, `versionName = "1.0"`.
- Generación con `./gradlew assembleRelease` o la herramienta de firma de Android Studio.

---

## 🛠️ Estructura del Proyecto

```
OFT-APP/
├── OFTAPP/                         # Proyecto Android Principal
│   ├── app/
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/com/example/oftapp/
│   │       │   │   ├── data/       # Room DB, DataStore, Retrofit FHIR API
│   │       │   │   ├── model/      # Entidades y Modelos de Dominio
│   │       │   │   ├── ui/         # Pantallas Compose, Visor PDF, Zoom Image, Tema
│   │       │   │   ├── util/       # TextToSpeechHelper
│   │       │   │   └── viewmodel/  # ViewModels MVVM
│   │       └── test/               # Pruebas Unitarias (MockK, JUnit)
│   │       └── androidTest/        # Pruebas Instrumentadas Compose UI Test
└── backend/                        # Backend Microservicio Spring Boot
    ├── pom.xml
    └── src/main/java/com/example/oftbackend/
        ├── config/                 # Swagger / OpenAPI Config
        ├── controller/             # Controladores REST API CRUD
        ├── model/                  # JPA Entities (Paciente, Examen)
        └── repository/             # Repositorios Spring Data JPA
```

---

## 🚦 Instrucciones para Ejecutar el Proyecto

### 1. Ejecución de la Aplicación Android
1. Abre la carpeta `OFTAPP/` en Android Studio (Ladybug / Koala o superior).
2. Sincroniza Gradle (*Sync Project with Gradle Files*).
3. Selecciona un emulador o dispositivo físico Android con API >= 24.
4. Presiona **Run 'app'**.

### 2. Ejecución del Backend Spring Boot (Opcional para API Local)
```bash
cd backend
mvnw spring-boot:run
```
Accede a la documentación interactiva Swagger UI en: `http://localhost:8080/swagger-ui.html`.

### 3. Ejecución de Pruebas Unitarias e Instrumentadas
```bash
# Pruebas Unitarias en JVM
./gradlew testDebugUnitTest

# Pruebas Instrumentadas en Emulador
./gradlew connectedCheck
```
