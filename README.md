# Daily Horoscope

[English](#english) | [Español](#español)

## English

Native Android application written in Kotlin for reading daily horoscopes for all 12 zodiac signs, with English and Spanish support.

### Features

- Zodiac sign grid with date ranges, illustrations, and individual backgrounds.
- Daily predictions with the date returned by the horoscope service.
- Language selector: system default, English, or Spanish.
- Automatic translation of predictions into Spanish, with English fallback if translation fails.
- Local cache per sign and language, displayed while fresh content loads and retained if the request fails.
- Refresh, retry, and sharing through Android's share menu.
- Google AdMob banner integration.

An internet connection is required to fetch new predictions, translate text, and load ads. Previously cached predictions may be from an earlier day.

### Project configuration

| Setting | Value |
| --- | --- |
| Application ID | `com.powerdevelop.dailyhoroscope` |
| Version | `1.0.1` (code `2`) |
| Minimum Android version | Android 5.0 / API 21 |
| Compile / target SDK | 35 / 35 |
| Android Gradle Plugin | 8.9.1 |
| Gradle wrapper | 8.11.1 |
| Kotlin | 2.0.21 |
| Java / Kotlin bytecode target | 11 |

The interface uses XML layouts, View Binding, AndroidX, and Material Components. HTTP requests use OkHttp; preferences and cached predictions use SharedPreferences.

### Setup

1. Clone or download this repository and open its root directory in Android Studio.
2. Use JDK 17 to run Gradle. The bytecode target of 11 does not replace this build requirement.
3. Install Android SDK Platform 35 and the SDK build tools through Android Studio's SDK Manager.
4. Set the SDK location using `ANDROID_HOME` or a root-level `local.properties` file. For example, on Windows:

   ```properties
   sdk.dir=C:/Users/YOUR_USER/AppData/Local/Android/Sdk
   ```

5. Keep a valid `app/google-services.json` matching the application ID; the Google Services plugin processes it during the build. For your own deployment, configure your Google services project and AdMob identifiers in `AndroidManifest.xml` and the activity layouts.
6. Sync Gradle, then run the `app` module on an emulator or connected Android device.

### Build and install an APK

Run from the repository root in Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
chmod +x gradlew
./gradlew assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

It is signed with a debug key and intended for testing. To build and install directly on a connected device or running emulator:

```powershell
.\gradlew.bat installDebug
```

For a release APK, run `assembleRelease` instead. The project does not configure release signing in Gradle; use Android Studio's **Generate Signed Bundle / APK** flow or configure a release signing configuration before distribution.

### Services and source layout

- `HoroscopeActivity.kt`: fetches predictions from `https://freehoroscopeapi.com/api/v1/get-horoscope/daily?sign=<sign>`, manages caching, and shares results.
- `HoroscopeTranslator.kt`: sends English prediction text to `translate.googleapis.com` for Spanish translation; returns the original text on failure.
- `MainActivity.kt` and `SignAdapter.kt`: zodiac selection and language settings entry point.
- `LocaleHelper.kt`: saves and applies the language preference.
- `app/src/main/res/layout/`: screen and sign-item layouts.
- `app/src/main/res/values/` and `values-es/`: English and Spanish strings.
- `app/src/main/res/drawable/`: zodiac artwork and backgrounds.
- `gradle/libs.versions.toml`: dependency version catalog.

Service addresses describe the current source configuration; their availability is not guaranteed by this repository.

### Development checks

```powershell
.\gradlew.bat testDebugUnitTest lintDebug
.\gradlew.bat connectedDebugAndroidTest
```

The second command requires a running emulator or connected device. The included tests are basic examples, not comprehensive feature coverage.

## Español

Aplicación nativa de Android desarrollada en Kotlin para consultar el horóscopo diario de los 12 signos del zodiaco, con soporte para inglés y español.

### Funciones

- Cuadrícula de signos con rangos de fechas, ilustraciones y fondos individuales.
- Predicciones diarias con la fecha que devuelve el servicio de horóscopos.
- Selector de idioma: predeterminado del sistema, inglés o español.
- Traducción automática de las predicciones al español; si falla, se muestra el texto original en inglés.
- Caché local por signo e idioma, visible mientras se carga contenido nuevo y conservada si falla la solicitud.
- Opciones para actualizar, reintentar y compartir mediante el menú de Android.
- Integración de banners de Google AdMob.

Se necesita conexión a internet para obtener nuevas predicciones, traducir textos y cargar anuncios. Las predicciones guardadas pueden corresponder a un día anterior.

### Configuración del proyecto

| Parámetro | Valor |
| --- | --- |
| ID de aplicación | `com.powerdevelop.dailyhoroscope` |
| Versión | `1.0.1` (código `2`) |
| Versión mínima de Android | Android 5.0 / API 21 |
| SDK de compilación / destino | 35 / 35 |
| Android Gradle Plugin | 8.9.1 |
| Gradle wrapper | 8.11.1 |
| Kotlin | 2.0.21 |
| Destino del bytecode Java / Kotlin | 11 |

La interfaz utiliza layouts XML, View Binding, AndroidX y Material Components. Las solicitudes HTTP usan OkHttp; las preferencias y las predicciones en caché se guardan con SharedPreferences.

### Preparación

1. Clona o descarga este repositorio y abre su carpeta raíz en Android Studio.
2. Utiliza JDK 17 para ejecutar Gradle. El destino de bytecode 11 no sustituye este requisito de compilación.
3. Instala Android SDK Platform 35 y las herramientas de compilación desde el SDK Manager de Android Studio.
4. Configura la ubicación del SDK con `ANDROID_HOME` o un archivo `local.properties` en la raíz. Por ejemplo, en Windows:

   ```properties
   sdk.dir=C:/Users/TU_USUARIO/AppData/Local/Android/Sdk
   ```

5. Conserva un archivo `app/google-services.json` válido que coincida con el ID de aplicación; el plugin Google Services lo procesa durante la compilación. Para tu propia distribución, configura tu proyecto de servicios de Google y los identificadores de AdMob en `AndroidManifest.xml` y los layouts de las actividades.
6. Sincroniza Gradle y ejecuta el módulo `app` en un emulador o dispositivo Android conectado.

### Generar e instalar un APK

Desde la raíz del repositorio, ejecuta en PowerShell de Windows:

```powershell
.\gradlew.bat assembleDebug
```

En macOS o Linux:

```bash
chmod +x gradlew
./gradlew assembleDebug
```

El APK de depuración se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Está firmado con una clave de depuración y está destinado a pruebas. Para compilar e instalar directamente en un dispositivo conectado o emulador en ejecución:

```powershell
.\gradlew.bat installDebug
```

Para generar un APK de lanzamiento, ejecuta `assembleRelease`. El proyecto no configura la firma de lanzamiento en Gradle; utiliza **Generate Signed Bundle / APK** en Android Studio o configura la firma de lanzamiento antes de distribuirlo.

### Servicios y estructura del código

- `HoroscopeActivity.kt`: consulta `https://freehoroscopeapi.com/api/v1/get-horoscope/daily?sign=<sign>`, gestiona la caché y comparte resultados.
- `HoroscopeTranslator.kt`: envía el texto de la predicción en inglés a `translate.googleapis.com` para traducirlo al español; devuelve el original si falla.
- `MainActivity.kt` y `SignAdapter.kt`: selección de signos y acceso a la configuración de idioma.
- `LocaleHelper.kt`: guarda y aplica la preferencia de idioma.
- `app/src/main/res/layout/`: layouts de pantallas y elementos de signos.
- `app/src/main/res/values/` y `values-es/`: textos en inglés y español.
- `app/src/main/res/drawable/`: ilustraciones y fondos del zodiaco.
- `gradle/libs.versions.toml`: catálogo de versiones de dependencias.

Las direcciones de los servicios describen la configuración actual del código; este repositorio no garantiza su disponibilidad.

### Comprobaciones de desarrollo

```powershell
.\gradlew.bat testDebugUnitTest lintDebug
.\gradlew.bat connectedDebugAndroidTest
```

El segundo comando requiere un emulador en ejecución o un dispositivo conectado. Las pruebas incluidas son ejemplos básicos y no ofrecen cobertura completa de las funciones.
