# HerreraSystemMobile

Aplicación Android nativa para administrar operaciones de Sorbetería Herrera. Está construida con Kotlin, Jetpack Compose y Material 3; consume la API REST de Herrera.

La arquitectura actual, los flujos principales, las capas, las rutas y las decisiones técnicas se describen en [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Requisitos y compilación

- Android Studio con su JDK incluido (JDK 11 como destino de bytecode).
- Android SDK 35.
- Gradle Wrapper incluido en el repositorio.

Desde Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

Desde macOS/Linux:

```bash
./gradlew :app:assembleDebug
```

La API está configurada en `data/remote/retrofit/RetrofitClient.kt`. Para cambiar el servidor, actualiza allí `API_ORIGIN`; no guardes credenciales de usuario ni secretos de producción en el cliente.
