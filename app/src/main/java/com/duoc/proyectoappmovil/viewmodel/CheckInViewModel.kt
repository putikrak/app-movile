package com.duoc.proyectoappmovil.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.proyectoappmovil.data.RegistroEmocional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CheckInViewModel : ViewModel() {

    // Lista simulada en memoria/persistencia local para el historial
    private val _historial = MutableStateFlow<List<RegistroEmocional>>(
        listOf(
            RegistroEmocional(
                nivelAnimo = 4,
                etiquetaAnimo = "Bien",
                categoria = "Estudios/Académico",
                nota = "Avancé bien con la entrega del proyecto de Android."
            )
        )
    )
    val historial: StateFlow<List<RegistroEmocional>> = _historial.asStateFlow()

    fun guardarRegistro(nivel: Int, etiqueta: String, categoria: String, nota: String) {
        val nuevoRegistro = RegistroEmocional(
            nivelAnimo = nivel,
            etiquetaAnimo = etiqueta,
            categoria = categoria,
            nota = nota
        )
        _historial.value = listOf(nuevoRegistro) + _historial.value
    }
}