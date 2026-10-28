# TVPlayerProyecto5F

Aplicación Android para reproducir canales de televisión mediante URLs de streaming y administrar una lista personalizada de canales.

## Funcionalidades

- Reproducción de streams mediante AndroidX Media3/ExoPlayer.
- Soporte explícito para listas HLS (`.m3u8`).
- Alta de canales con nombre, URL del stream y URL del logo.
- Edición y eliminación de canales mediante pulsación prolongada.
- Persistencia local de los canales con `SharedPreferences` y Gson.
- Carga de logos desde URL con Glide.
- Reproducción automática del primer canal guardado.

## Requisitos

- Android Studio compatible con Android Gradle Plugin `8.13.0`.
- JDK 8 o superior.
- Android SDK con API 36.
- Dispositivo o emulador con Android 7.0 (API 24) o superior.
- Conexión a Internet para reproducir streams y cargar logos.

## Compilación y ejecución

1. Clona el repositorio y ábrelo en Android Studio:

   ```bash
   git clone https://github.com/YaomaHG/TVPlayerProyecto5F.git
   cd TVPlayerProyecto5F
   ```

2. Sincroniza el proyecto con Gradle.
3. Conecta un dispositivo Android o inicia un emulador.
4. Ejecuta la configuración `app` desde Android Studio.

También puedes compilar desde la terminal:

```bash
./gradlew assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/`.

## Uso

1. Pulsa el botón `+` para añadir un canal.
2. Introduce el nombre, la URL del stream y, opcionalmente, la URL del logo.
3. Guarda el canal para incorporarlo a la lista.
4. Pulsa un canal para iniciar su reproducción.
5. Mantén pulsado un canal para editarlo o eliminarlo.

Las URLs de stream deben ser accesibles desde el dispositivo. Para streams HLS se recomienda utilizar URLs con extensión `.m3u8`.

## Estructura principal

```text
app/src/main/
├── java/com/example/tvplayerproyecto5f/
│   ├── MainActivity.kt              # Reproductor y lista de canales
│   ├── AddEditChannelActivity.kt    # Alta y edición de canales
│   └── ChannelAdapter.kt            # Adaptador del RecyclerView
├── res/layout/                     # Interfaces de reproducción y formularios
└── AndroidManifest.xml             # Permisos y actividades
```

## Tecnologías

- Kotlin
- Android SDK 36
- AndroidX AppCompat, Activity, Core KTX y ConstraintLayout
- Material Components
- AndroidX Media3 ExoPlayer
- Gson
- Glide

## Pruebas

Para ejecutar las pruebas unitarias:

```bash
./gradlew test
```

Para ejecutar las pruebas instrumentadas con un dispositivo o emulador conectado:

```bash
./gradlew connectedAndroidTest
```

## Licencia

Este proyecto no incluye actualmente un archivo de licencia.
