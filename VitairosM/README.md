# VITAIROS-M · MVP Android (DSY1105)

Kotlin + Jetpack Compose + Room (offline-first) + Navigation + WorkManager.

## Abrir y ejecutar
1. Android Studio (Koala o superior) → File > Open → carpeta VitairosM.
2. Esperar el Gradle Sync. Ejecutar en emulador/teléfono Android 8.0+ (API 26+).
3. LOGO OFICIAL: copiar la imagen oficial del banner como
   app/src/main/res/drawable/logo_vitairos.png (el manual prohíbe reescribirlo con tipografía).
4. Tipografía Outfit (opcional): descargar de Google Fonts, poner en res/font y asignar en Typography.

## Accesos de prueba
- Usuario demo:     demo@vitairos.cl / demo1234  (botón "Ingresar como Usuario Demo")
- Profesional demo: pro@vitairos.cl  / pro1234

## Estructura (app/src/main/java/cl/duoc/vitairosm)
Data.kt (entidades Room + DAOs) · Seed.kt (dataset sintético) · Logic.kt (crisis, árbol de chat, utilidades)
AppViewModel.kt · Reminders.kt (WorkManager) · Ui.kt (tema/marca) · MainActivity.kt (navegación)
Screens*.kt (pantallas)
