package com.duoc.proyectoappmovil.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.proyectoappmovil.ui.theme.NavyPrincipal

@Composable
fun EmergenciaScreen(
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFEFE))
            .padding(20.dp)
    ) {
        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "← Volver al inicio",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrincipal,
                modifier = Modifier.clickable { onVolver() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Canales de Ayuda Urgente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE53935)
        )
        Text(
            text = "Si estás atravesando una crisis o necesitas apoyo inmediato, no estás solo/a.",
            fontSize = 13.sp,
            color = Color(0xFF536477)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta Principal *4141 (Línea de Prevención)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Línea *4141 - Prevención del Suicidio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFE53935)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Atención gratuita, confidencial y disponible las 24 horas del día, los 7 días de la semana (MINSAL Chile).",
                    fontSize = 12.sp,
                    color = Color(0xFF536477)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Recurso Nativo: Dispara el Intent del Marcador Telefónico del Celular
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:*4141")
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📞 Llamar al *4141 ahora", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta Secundaria Salud Responde
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F8FB)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Salud Responde - 600 360 7777",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NavyPrincipal
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Orientación médica y psicológica general las 24 horas del día.",
                    fontSize = 12.sp,
                    color = Color(0xFF536477)
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:6003607777")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📞 Llamar a Salud Responde", color = NavyPrincipal)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Aviso de seguridad
        Text(
            text = "Recordatorio: VITAIROS-M es una plataforma de apoyo y continuidad digital. Ante emergencias médicas de riesgo vital inmediato, dirígete al centro de salud o servicio de urgencias más cercano.",
            fontSize = 11.sp,
            color = Color(0xFF7A8B9E)
        )
    }
}