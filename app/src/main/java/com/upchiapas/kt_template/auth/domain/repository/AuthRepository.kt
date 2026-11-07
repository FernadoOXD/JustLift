package com.upchiapas.kt_template.auth.domain.repository

import com.upchiapas.kt_template.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun register(name: String, email: String, password: String): User
}