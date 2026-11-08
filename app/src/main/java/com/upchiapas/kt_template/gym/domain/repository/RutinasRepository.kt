package com.upchiapas.kt_template.gym.domain.repository

import com.upchiapas.kt_template.gym.domain.model.Rutina

interface RutinasRepository {
    suspend fun getRutinas(): List<Rutina>
    suspend fun getRutina(id: Int): Rutina
    suspend fun createRutina(rutina: Rutina): Rutina
    suspend fun updateRutina(rutina: Rutina): Rutina
    suspend fun deleteRutina(id: Int)
}