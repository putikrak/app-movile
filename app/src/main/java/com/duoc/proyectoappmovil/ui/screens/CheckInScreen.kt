package com.duoc.proyectoappmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.proyectoappmovil.ui.theme.FondoSecundario
import com.duoc.proyectoappmovil.ui.theme.NavyPrincipal
import com.duoc.proyectoappmovil.ui.theme.VerdeContinuidad

@Composable
fun CheckInScreen(
    onGuardar: (Int, String, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var nivelSeleccionado by remember { mutableStateOf(3) }
    var categoriaSeleccionada by remember { mutableStateOf("Estudios/Académico") }
    var notaText by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf(false) }

    val opcionesAnimo = listOf(
        1 to "😔 Muy bajo",
        2 to "😕 Bajo",
        3 to "😐 Neutro",
        4 to "🙂 Bien",
        5 to "😄 Excelente"
    )

    val categorias = listOf(
        "Estudios/Académico",
        "Trabajo",
        "Relaciones/Pareja",
        "Familia",
        "Sueño/Descanso",
        "Salud/Bienestar",
        "Otro"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFEFE))
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "← Volver",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrincipal,
                modifier = Modifier.clickable { onVolver() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Check-in Emocional",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrincipal
        )
        Text(
            text = "Registra cómo te sientes en este momento",
            fontSize = 14.sp,
            color = Color(0xFF536477)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de Ánimo (5 niveles obligatorios según Key User)
        Text(
            text = "1. Estado de ánimo *",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NavyPrincipal
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            opcionesAnimo.forEach { (nivel, etiqueta) ->
                val seleccionado = nivelSeleccionado == nivel
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionado) Color(0xFFDDF6F0) else FondoSecundario
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (seleccionado) 2.dp else 0.dp,
                            color = if (seleccionado) VerdeContinuidad else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { nivelSeleccionado = nivel },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = etiqueta,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(14.dp),
                        color = NavyPrincipal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Categoría de seguimiento
        Text(
            text = "2. Ámbito o categoría de contexto",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NavyPrincipal
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            categorias.forEach { cat ->
                val sel = categoriaSeleccionada == cat
                RadioButtonOption(
                    texto = cat,
                    seleccionado = sel,
                    onClick = { categoriaSeleccionada = cat }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nota opcional
        Text(
            text = "3. Nota personal (opcional)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NavyPrincipal
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notaText,
            onValueChange = { notaText = it },
            placeholder = { Text("Escribe brevemente qué influyó en tu ánimo...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (mensajeExito) {
            Text(
                text = "¡Gracias por registrarlo! Tu registro se ha guardado en la bitácora.",
                color = VerdeContinuidad,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                val etiqueta = opcionesAnimo.first { it.first == nivelSeleccionado }.second
                onGuardar(nivelSeleccionado, etiqueta, categoriaSeleccionada, notaText)
                mensajeExito = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrincipal),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Check-in")
        }
    }
}

@Composable
fun RadioButtonOption(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        RadioButton(selected = seleccionado, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, fontSize = 14.sp, color = NavyPrincipal)
    }
}