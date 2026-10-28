package com.duoc.proyectoappmovil.ui.navigation

import androidx.compose.runtime.*
import com.duoc.proyectoappmovil.ui.screens.CheckInScreen
import com.duoc.proyectoappmovil.ui.screens.EmergenciaScreen
import com.duoc.proyectoappmovil.ui.screens.HistorialScreen
import com.duoc.proyectoappmovil.ui.screens.HomeScreen
import com.duoc.proyectoappmovil.ui.screens.OnboardingScreen
import com.duoc.proyectoappmovil.ui.screens.OrientacionScreen
import com.duoc.proyectoappmovil.ui.screens.ProfesionalesScreen
import com.duoc.proyectoappmovil.viewmodel.CheckInViewModel

@Composable
fun AppNavigation() {
    var pantallaActual by remember { mutableStateOf("onboarding") }
    val checkInViewModel = remember { CheckInViewModel() }
    val listaHistorial by checkInViewModel.historial.collectAsState()

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
        "historial" -> {
            HistorialScreen(
                historial = listaHistorial,
                onVolver = {
                    pantallaActual = "home"
                }
            )
        }
        "orientacion" -> {
            OrientacionScreen(
                onVolver = {
                    pantallaActual = "home"
                }
            )
        }
        "profesionales" -> {
            ProfesionalesScreen(
                onVolver ={
                    pantallaActual = "home"
                }
            )
        }
        "emergencia" -> {
            EmergenciaScreen(
                onVolver ={
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