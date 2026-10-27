package com.duoc.proyectoappmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.proyectoappmovil.ui.theme.FondoSecundario
import com.duoc.proyectoappmovil.ui.theme.NavyPrincipal

@Composable
fun HomeScreen(
    onNavegarA: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            BotonEmergencia(onClick = { onNavegarA("emergencia") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEFEFE))
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Saludo
            Text(
                text = "¡Hola, Usuario Demo! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrincipal
            )
            Text(
                text = "Continuidad y acompañamiento personal",
                fontSize = 14.sp,
                color = Color(0xFF536477)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta 1: Check-in Emocional Diario
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDDF6F0)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavegarA("checkin") }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "😊", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¿Cómo te sientes hoy?",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrincipal,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Toca aquí para realizar tu registro emocional diario",
                            fontSize = 12.sp,
                            color = Color(0xFF536477)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta 2: Orientación Conversacional
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrincipal),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavegarA("orientacion") }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💬 Orientación y Acompañamiento",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Habla con VITAIROS-M sobre lo que te preocupa hoy.",
                        fontSize = 12.sp,
                        color = Color(0xFFE1F3FE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta 3: Tu Próximo Paso
            Card(
                colors = CardDefaults.cardColors(containerColor = FondoSecundario),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 Tu próximo paso sugerido",
                        fontWeight = FontWeight.Bold,
                        color = NavyPrincipal,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pausa de respiración consciente (2 minutos)",
                        fontSize = 13.sp,
                        color = Color(0xFF536477)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Accesos Rápidos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FondoSecundario),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavegarA("profesionales") }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "👨‍⚕️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Profesionales",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyPrincipal
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = FondoSecundario),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavegarA("historial") }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📜", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bitácora",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyPrincipal
                        )
                    }
                }
            }
        }
    }
}