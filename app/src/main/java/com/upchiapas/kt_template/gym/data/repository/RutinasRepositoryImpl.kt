package com.upchiapas.kt_template.gym.data.repository

import com.upchiapas.kt_template.gym.data.datasource.RutinasApiService
import com.upchiapas.kt_template.gym.data.mapper.toDomain
import com.upchiapas.kt_template.gym.data.mapper.toDto
import com.upchiapas.kt_template.gym.domain.model.Rutina
import com.upchiapas.kt_template.gym.domain.model.RutinasException
import com.upchiapas.kt_template.gym.domain.repository.RutinasRepository
import retrofit2.HttpException
import java.io.IOException
import com.upchiapas.kt_template.core.storage.AuthPreferences

class RutinasRepositoryImpl(
    private val api: RutinasApiService,
    private val authPreferences: AuthPreferences
) : RutinasRepository {

    override suspend fun getRutinas(): List<Rutina> = safeCall {
        api.getRutinas(currentUserId()).map { it.toDomain() }
    }

    override suspend fun getRutina(id: Int): Rutina = safeCall {
        api.getRutina(id).toDomain()
    }

    override suspend fun createRutina(rutina: Rutina): Rutina = safeCall {
        api.createRutina(rutina.toDto(currentUserId())).toDomain()
    }

    override suspend fun updateRutina(rutina: Rutina): Rutina = safeCall {
        api.updateRutina(rutina.id, rutina.toDto(currentUserId())).toDomain()
    }

    override suspend fun deleteRutina(id: Int): Unit = safeCall {
        api.deleteRutina(id)
    }

    private fun currentUserId(): Int =
        authPreferences.getUserId()
            ?: throw RutinasException("Tu sesión expiró. Cierra sesión e inicia de nuevo")

    private suspend fun <T> safeCall(block: suspend () -> T): T {
        try {
            return block()
        } catch (e: HttpException) {
            throw RutinasException(
                when (e.code()) {
                    401 -> "Tu sesión expiró. Cierra sesión e inicia de nuevo"
                    403 -> "No tienes permiso para modificar esta rutina"
                    404 -> "La rutina ya no existe"
                    else -> "Error del servidor (${e.code()})"
                }
            )
        } catch (e: IOException) {
            throw RutinasException("No se pudo conectar con el servidor. Revisa tu conexión")
        }
    }
}