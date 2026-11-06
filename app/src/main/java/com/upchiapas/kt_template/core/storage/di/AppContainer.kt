package com.upchiapas.kt_template.core.storage.di

import android.content.Context
import com.upchiapas.kt_template.core.storage.network.AuthInterceptor
import com.upchiapas.kt_template.core.storage.AuthPreferences
import com.upchiapas.kt_template.core.storage.AuthPreferencesImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

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
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    //nota personal
    // Se usará desde el Paso 5 en adelante para crear los ApiService
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}