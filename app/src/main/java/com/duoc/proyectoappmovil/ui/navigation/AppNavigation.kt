package com.duoc.proyectoappmovil.ui.navigation

import androidx.compose.runtime.*
import com.duoc.proyectoappmovil.ui.screens.CheckInScreen
import com.duoc.proyectoappmovil.ui.screens.HomeScreen
import com.duoc.proyectoappmovil.ui.screens.OnboardingScreen
import com.duoc.proyectoappmovil.viewmodel.CheckInViewModel

@Composable
fun AppNavigation() {
    var pantallaActual by remember { mutableStateOf("onboarding") }
    // Usamos remember para mantener la instancia del ViewModel sin librerías externas de Gradle
    val checkInViewModel = remember { CheckInViewModel() }

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
        "checkin" -> {
            CheckInScreen(
                onGuardar = { nivel, etiqueta, categoria, nota ->
                    checkInViewModel.guardarRegistro(nivel, etiqueta, categoria, nota)
                },
                onVolver = {
                    pantallaActual = "home"
                }
            )
        }
        else -> {
            HomeScreen(
                onNavegarA = { ruta ->
                    pantallaActual = ruta
                }
            )
        }
    }
}