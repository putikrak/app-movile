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
import com.duoc.proyectoappmovil.ui.theme.VerdeContinuidad

data class Profesional(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val modalidad: String,
    val horariosDisponibles: List<String>
)

@Composable
fun ProfesionalesScreen(
    onVolver: () -> Unit
) {
    val profesionales = remember {
        listOf(
            Profesional(
                id = 1,
                nombre = "Dra. Natalia Silva",
                especialidad = "Psicología Clínica y Ansiedad Académica",
                modalidad = "Teleatención / Online",
                horariosDisponibles = listOf("Mañana 10:00 hrs", "Tarde 15:30 hrs", "Tarde 17:00 hrs")
            ),
            Profesional(
                id = 2,
                nombre = "Ps. Carlos Mendoza",
                especialidad = "Orientación Vocacional y Gestión del Estrés",
                modalidad = "Presencial / Teleatención",
                horariosDisponibles = listOf("Mañana 11:30 hrs", "Tarde 16:00 hrs")
            ),
            Profesional(
                id = 3,
                nombre = "Mg. Valentina Reyes",
                especialidad = "Tutora de Bienestar y Hábitos de Estudio",
                modalidad = "Teleatención / Online",
                horariosDisponibles = listOf("Tarde 14:00 hrs", "Tarde 18:00 hrs")
            )
        )
    }

    var profesionalSeleccionado by remember { mutableStateOf<Profesional?>(null) }
    var horarioSeleccionado by remember { mutableStateOf("") }
    var citaConfirmada by remember { mutableStateOf(false) }

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
            text = "Red de Profesionales",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrincipal
        )
        Text(
            text = "Agenda una sesión de apoyo o acompañamiento",
            fontSize = 13.sp,
            color = Color(0xFF536477)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (citaConfirmada && profesionalSeleccionado != null) {
            // Confirmación de Cita
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDDF6F0)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "¡Cita Agendada con Éxito! 🎉",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavyPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Profesional: ${profesionalSeleccionado?.nombre}",
                        fontSize = 14.sp,
                        color = NavyPrincipal
                    )
                    Text(
                        text = "Horario: $horarioSeleccionado",
                        fontSize = 14.sp,
                        color = NavyPrincipal
                    )
                    Text(
                        text = "Modalidad: ${profesionalSeleccionado?.modalidad}",
                        fontSize = 13.sp,
                        color = Color(0xFF536477)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            citaConfirmada = false
                            profesionalSeleccionado = null
                            horarioSeleccionado = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrincipal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al catálogo")
                    }
                }
            }
        } else {
            // Lista de Profesionales
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(profesionales) { prof ->
                    val esElSeleccionado = profesionalSeleccionado?.id == prof.id

                    Card(
                        colors = CardDefaults.cardColors(containerColor = FondoSecundario),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = prof.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NavyPrincipal
                            )
                            Text(
                                text = prof.especialidad,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VerdeContinuidad
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📍 ${prof.modalidad}",
                                fontSize = 12.sp,
                                color = Color(0xFF536477)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Horarios disponibles:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrincipal
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                prof.horariosDisponibles.forEach { hor ->
                                    val esHorarioSel = esElSeleccionado && horarioSeleccionado == hor
                                    FilterChip(
                                        selected = esHorarioSel,
                                        onClick = {
                                            profesionalSeleccionado = prof
                                            horarioSeleccionado = hor
                                        },
                                        label = { Text(hor, fontSize = 11.sp) }
                                    )
                                }
                            }

                            if (esElSeleccionado && horarioSeleccionado.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { citaConfirmada = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrincipal),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Confirmar Cita para $horarioSeleccionado")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}