package com.upchiapas.kt_template.core.storage.network

import com.upchiapas.kt_template.core.storage.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = authPreferences.getToken()

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        return chain.proceed(newRequest)
    }
}