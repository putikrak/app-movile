package com.duoc.proyectoappmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.proyectoappmovil.ui.theme.NavyPrincipal
import com.duoc.proyectoappmovil.ui.theme.VerdeContinuidad

@Composable
fun OnboardingScreen(
    onIngresar: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFEFE))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "VITAIROS-M",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrincipal
        )
        Text(
            text = "SALUD MENTAL DIGITAL",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = VerdeContinuidad,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de Aviso Ético y Legal (Requerimiento Key User)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F8FB)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Aviso Importante",
                    fontWeight = FontWeight.Bold,
                    color = NavyPrincipal,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "VITAIROS-M orienta y acompaña. No realiza diagnósticos, no prescribe tratamientos ni reemplaza la atención profesional.",
                    fontSize = 12.sp,
                    color = Color(0xFF536477)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de Formulario con Validación
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico simulado") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMensaje, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (correo.contains("@") && clave.length >= 4) {
                    onIngresar()
                } else {
                    errorMensaje = "Ingresa un correo válido y clave de al menos 4 caracteres"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrincipal),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Demo para evaluación rápida docente
        OutlinedButton(
            onClick = onIngresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar como Usuario Demo", color = VerdeContinuidad)
        }
    }
}
