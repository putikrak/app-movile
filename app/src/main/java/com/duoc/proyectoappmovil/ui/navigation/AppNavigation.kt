package com.duoc.proyectoappmovil.ui.navigation

import androidx.compose.runtime.*
import com.duoc.proyectoappmovil.ui.screens.HomeScreen
import com.duoc.proyectoappmovil.ui.screens.OnboardingScreen

@Composable
fun AppNavigation() {
    var pantallaActual by remember { mutableStateOf("onboarding") }

    when (pantallaActual) {
        "onboarding" -> {
            OnboardingScreen(
                onIngresar = {
                    pantallaActual = "home"
                }
            )
        }
        "home" -> {
            HomeScreen(
                onNavegarA = { ruta ->
                    pantallaActual = ruta
                }
            )
        }
        else -> {
            // Manejo temporal para rutas en desarrollo
            HomeScreen(
                onNavegarA = { ruta ->
                    pantallaActual = ruta
                }
            )
        }
    }
}