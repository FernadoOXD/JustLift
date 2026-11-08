package com.upchiapas.kt_template.gym.domain.model

data class Rutina(
    val id: Int = 0,
    val nombre: String,
    val grupoMuscular: String,
    val series: Int,
    val repeticiones: Int,
    val activa: Boolean
)