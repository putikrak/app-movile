package com.duoc.proyectoappmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

data class MensajeChat(
    val emisor: String, // "bot" o "usuario"
    val texto: String,
    val esProximoPaso: Boolean = false
)

@Composable
fun OrientacionScreen(
    onVolver: () -> Unit
) {
    var mensajes by remember {
        mutableStateOf(
            listOf(
                MensajeChat("bot", "¡Hola! Soy tu asistente de orientación en VITAIROS-M. ¿Sobre qué te gustaría reflexionar o conversar hoy?")
            )
        )
    }

    val opcionesGuia = listOf(
        "Tengo mucha carga académica/estrés",
        "Me cuesta conciliar el sueño",
        "Necesito un ejercicio de pausa breve",
        "Quiero organizar mi rutina de estudio"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFEFE))
            .padding(20.dp)
    ) {
        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "← Volver",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrincipal,
                modifier = Modifier.clickable { onVolver() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Orientación Conversacional",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrincipal
        )
        Text(
            text = "Acompañamiento guiado y pautas de acción",
            fontSize = 13.sp,
            color = Color(0xFF536477)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Historial de la conversación
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(mensajes) { msg ->
                if (msg.esProximoPaso) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDDF6F0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🎯 Tu próximo paso sugerido",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NavyPrincipal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.texto,
                                fontSize = 13.sp,
                                color = Color(0xFF536477)
                            )
                        }
                    }
                } else {
                    val esBot = msg.emisor == "bot"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (esBot) Arrangement.Start else Arrangement.End
                    ) {
                        Surface(
                            color = if (esBot) FondoSecundario else NavyPrincipal,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.texto,
                                color = if (esBot) NavyPrincipal else Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Opciones de respuesta rápida
        Text(
            text = "Selecciona un tema para continuar:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = NavyPrincipal
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            opcionesGuia.forEach { opcion ->
                OutlinedButton(
                    onClick = {
                        val nuevosMensajes = mensajes.toMutableList()
                        nuevosMensajes.add(MensajeChat("usuario", opcion))

                        when (opcion) {
                            "Tengo mucha carga académica/estrés" -> {
                                nuevosMensajes.add(
                                    MensajeChat("bot", "Es natural sentir saturación cuando hay entregas simultáneas. Dividir tus metas en bloques pequeños disminuye la ansiedad.")
                                )
                                nuevosMensajes.add(
                                    MensajeChat("bot", "Prueba estudiar 25 minutos enfocado y toma 5 minutos de pausa activa sin pantallas.", esProximoPaso = true)
                                )
                            }
                            "Me cuesta conciliar el sueño" -> {
                                nuevosMensajes.add(
                                    MensajeChat("bot", "El descanso adecuado es fundamental para consolidar el aprendizaje y regular el estado de ánimo.")
                                )
                                nuevosMensajes.add(
                                    MensajeChat("bot", "Desconecta tus pantallas 30 minutos antes de dormir y lee algo liviano.", esProximoPaso = true)
                                )
                            }
                            "Necesito un ejercicio de pausa breve" -> {
                                nuevosMensajes.add(
                                    MensajeChat("bot", "Una pausa consciente ayuda a restablecer la atención y calmar el sistema nervioso.")
                                )
                                nuevosMensajes.add(
                                    MensajeChat("bot", "Inhala profundo en 4 segundos, sostén 4 segundos y exhala suavemente en 6 segundos.", esProximoPaso = true)
                                )
                            }
                            else -> {
                                nuevosMensajes.add(
                                    MensajeChat("bot", "Establecer prioridades te otorga mayor control sobre tus tiempos académicos y personales.")
                                )
                                nuevosMensajes.add(
                                    MensajeChat("bot", "Anota hoy solo las 3 tareas más importantes que debes resolver mañana.", esProximoPaso = true)
                                )
                            }
                        }
                        mensajes = nuevosMensajes
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = opcion,
                        fontSize = 12.sp,
                        color = NavyPrincipal
                    )
                }
            }
        }
    }
}