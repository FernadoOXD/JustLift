package com.upchiapas.kt_template.auth.domain.validation

object AuthValidator {

    const val MIN_PASSWORD_LENGTH = 8

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /** Devuelve el mensaje de error, o null si el correo es válido. */
    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "El correo es obligatorio"
        !EMAIL_REGEX.matches(email.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    /** Para registro: obligatoria y de al menos 8 caracteres chaval. */
    fun validateNewPassword(password: String): String? = when {
        password.isBlank() -> "La contraseña es obligatoria"
        password.length < MIN_PASSWORD_LENGTH ->
            "Mínimo $MIN_PASSWORD_LENGTH caracteres"
        else -> null
    }
}