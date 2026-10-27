package com.duoc.proyectoappmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.duoc.proyectoappmovil.ui.navigation.AppNavigation
import com.duoc.proyectoappmovil.ui.theme.FondoPrincipal
import com.duoc.proyectoappmovil.ui.theme.ProyectoAppMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoAppMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoPrincipal
                ) {
                    AppNavigation()
                }
            }
        }
    }
}