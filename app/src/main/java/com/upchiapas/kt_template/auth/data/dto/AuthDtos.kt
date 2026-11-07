package com.upchiapas.kt_template.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val name: String? = null
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val user: UserDto
)