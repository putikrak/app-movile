package com.duoc.proyectoappmovil.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object CheckIn : Screen("checkin")
    object Historial : Screen("historial")
    object Orientacion : Screen("orientacion")
    object Profesionales : Screen("profesionales")
    object Emergencia : Screen("emergencia")
}