package com.upchiapas.kt_template.core.storage

import android.content.Context
import android.content.SharedPreferences

interface AuthPreferences {
    fun saveSession(token: String, userId: Int)
    fun getToken(): String?
    fun getUserId(): Int?
    fun clearSession()
    fun hasToken(): Boolean
}

class AuthPreferencesImpl(context: Context) : AuthPreferences {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun saveSession(token: String, userId: Int) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun getUserId(): Int? =
        if (prefs.contains(KEY_USER_ID)) prefs.getInt(KEY_USER_ID, -1) else null

    override fun clearSession() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER_ID).apply()
    }

    override fun hasToken(): Boolean = !getToken().isNullOrBlank()

    private companion object {
        const val KEY_TOKEN = "KEY_TOKEN"
        const val KEY_USER_ID = "KEY_USER_ID"
    }
}