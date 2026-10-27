package com.duoc.proyectoappmovil.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RegistroEmocional(
    val id: Long = System.currentTimeMillis(),
    val nivelAnimo: Int,             // 1 a 5 (Muy bajo a Excelente)
    val etiquetaAnimo: String,       // Ej: "Excelente", "Neutro"
    val categoria: String,          // Ej: "Estudios", "Trabajo", "Sueño"
    val nota: String = "",           // Nota u observación personal (opcional)
    val fechaHora: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
)