package com.duoc.proyectoappmovil.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BotonEmergencia(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFFB41515),
        contentColor = Color.White,
        shape = RoundedCornerShape(50.dp),
        icon = { Text("⚠️", fontSize = 18.sp) },
        text = { Text("Ayuda Urgente", fontWeight = FontWeight.Bold) },
        modifier = Modifier.padding(16.dp)
    )
}