package com.upchiapas.kt_template.gym.domain.validation

object RutinaValidator {

    const val MIN_NOMBRE_LENGTH = 3
    val SERIES_RANGE = 1..20
    val REPETICIONES_RANGE = 1..100

    /** Cada función devuelve el mensaje de error, o null si el valor es válido. */

    fun validateNombre(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre es obligatorio"
        nombre.trim().length < MIN_NOMBRE_LENGTH -> "Mínimo $MIN_NOMBRE_LENGTH caracteres"
        else -> null
    }

    fun validateGrupoMuscular(grupo: String): String? =
        if (grupo.isBlank()) "El grupo muscular es obligatorio" else null

    fun validateSeries(series: String): String? = validateInRange(series, SERIES_RANGE)

    fun validateRepeticiones(repeticiones: String): String? =
        validateInRange(repeticiones, REPETICIONES_RANGE)

    private fun validateInRange(value: String, range: IntRange): String? {
        if (value.isBlank()) return "Este campo es obligatorio"
        val number = value.trim().toIntOrNull() ?: return "Ingresa un número válido"
        return if (number in range) null else "Debe estar entre ${range.first} y ${range.last}"
    }
}