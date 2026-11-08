package com.upchiapas.kt_template.gym.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RutinaDto(
    val id: Int? = null,
    val nombre: String,
    val grupoMuscular: String,
    val series: Int,
    val repeticiones: Int,
    val activa: Boolean
)