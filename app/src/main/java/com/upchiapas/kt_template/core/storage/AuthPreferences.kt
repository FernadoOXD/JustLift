package com.upchiapas.kt_template.core.storage



import android.content.Context
import android.content.SharedPreferences

interface AuthPreferences {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
    fun hasToken(): Boolean
}

class AuthPreferencesImpl(context: Context) : AuthPreferences {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit().putString("KEY_TOKEN", token).apply()
    }

    override fun getToken(): String? = prefs.getString("KEY_TOKEN", null)

    override fun clearToken() {
        prefs.edit().remove("KEY_TOKEN").apply()
    }

    override fun hasToken(): Boolean = !getToken().isNullOrBlank()
}