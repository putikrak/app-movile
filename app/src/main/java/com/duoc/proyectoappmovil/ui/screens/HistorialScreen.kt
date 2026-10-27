package com.duoc.proyectoappmovil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.proyectoappmovil.data.RegistroEmocional
import com.duoc.proyectoappmovil.ui.theme.FondoSecundario
import com.duoc.proyectoappmovil.ui.theme.NavyPrincipal
import com.duoc.proyectoappmovil.ui.theme.VerdeContinuidad

@Composable
fun HistorialScreen(
    historial: List<RegistroEmocional>,
    onVolver: () -> Unit
) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bitácora de Bienestar",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrincipal
        )
        Text(
            text = "Historial de tus registros emocionales",
            fontSize = 14.sp,
            color = Color(0xFF536477)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (historial.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes registros en tu bitácora.",
                    color = Color(0xFF536477),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(historial) { registro ->
                    TarjetaRegistro(registro = registro)
                }
            }
        }
    }
}

@Composable
fun TarjetaRegistro(registro: RegistroEmocional) {
    Card(
        colors = CardDefaults.cardColors(containerColor = FondoSecundario),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = registro.etiquetaAnimo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyPrincipal
                )
                Text(
                    text = registro.fechaHora,
                    fontSize = 11.sp,
                    color = Color(0xFF536477)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = Color(0xFFDDF6F0),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = registro.categoria,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VerdeContinuidad,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            if (registro.nota.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${registro.nota}\"",
                    fontSize = 13.sp,
                    color = Color(0xFF536477)
                )
            }
        }
    }
}