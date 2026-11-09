package com.upchiapas.kt_template.gym.data.mapper

import com.upchiapas.kt_template.gym.data.dto.RutinaDto
import com.upchiapas.kt_template.gym.domain.model.Rutina

fun RutinaDto.toDomain(): Rutina = Rutina(
    id = this.id ?: 0,
    nombre = this.nombre,
    grupoMuscular = this.grupoMuscular,
    series = this.series,
    repeticiones = this.repeticiones,
    activa = this.activa
)

fun Rutina.toDto(userId: Int): RutinaDto = RutinaDto(
    id = if (this.id == 0) null else this.id,
    userId = userId,
    nombre = this.nombre,
    grupoMuscular = this.grupoMuscular,
    series = this.series,
    repeticiones = this.repeticiones,
    activa = this.activa
)