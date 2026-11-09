package com.upchiapas.kt_template.core.di

import android.content.Context
import com.upchiapas.kt_template.auth.data.datasource.AuthApiService
import com.upchiapas.kt_template.auth.data.repository.AuthRepositoryImpl
import com.upchiapas.kt_template.auth.domain.repository.AuthRepository
import com.upchiapas.kt_template.core.network.AuthInterceptor
import com.upchiapas.kt_template.core.storage.AuthPreferences
import com.upchiapas.kt_template.core.storage.AuthPreferencesImpl
import com.upchiapas.kt_template.gym.data.datasource.RutinasApiService
import com.upchiapas.kt_template.gym.data.repository.RutinasRepositoryImpl
import com.upchiapas.kt_template.gym.domain.repository.RutinasRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {

    val authPreferences: AuthPreferences by lazy {
        AuthPreferencesImpl(context)
    }

    private val authInterceptor by lazy {
        AuthInterceptor(authPreferences)
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://justlift-backend.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, authPreferences)
    }

    private val rutinasApiService: RutinasApiService by lazy {
        retrofit.create(RutinasApiService::class.java)
    }

    val rutinasRepository: RutinasRepository by lazy {
        RutinasRepositoryImpl(rutinasApiService, authPreferences)
    }
}