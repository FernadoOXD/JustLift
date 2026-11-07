package com.upchiapas.kt_template.auth.data.repository

import com.upchiapas.kt_template.auth.data.datasource.AuthApiService
import com.upchiapas.kt_template.auth.data.dto.LoginRequestDto
import com.upchiapas.kt_template.auth.data.dto.RegisterRequestDto
import com.upchiapas.kt_template.auth.data.mapper.toDomain
import com.upchiapas.kt_template.auth.domain.model.AuthException
import com.upchiapas.kt_template.auth.domain.model.User
import com.upchiapas.kt_template.auth.domain.repository.AuthRepository
import com.upchiapas.kt_template.core.storage.AuthPreferences
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): User =
        authCall(badRequestMessage = "Correo o contraseña incorrectos") {
            val response = api.login(LoginRequestDto(email, password))
            authPreferences.saveToken(response.accessToken)
            response.user.toDomain()
        }

    override suspend fun register(name: String, email: String, password: String): User =
        authCall(badRequestMessage = "No se pudo crear la cuenta. ¿Ya existe ese correo?") {
            val response = api.register(RegisterRequestDto(email, password, name))
            authPreferences.saveToken(response.accessToken)
            response.user.toDomain()
        }

    // Traduce errores técnicos a mensajes entendibles (DRY: lo usan login y register)
    private suspend fun <T> authCall(badRequestMessage: String, block: suspend () -> T): T {
        try {
            return block()
        } catch (e: HttpException) {
            throw AuthException(
                if (e.code() == 400) badRequestMessage
                else "Error del servidor (${e.code()})"
            )
        } catch (e: IOException) {
            throw AuthException("No se pudo conectar con el servidor. Revisa tu conexión")
        }
    }
}