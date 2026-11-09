package com.upchiapas.kt_template.gym.presentation.form

data class RutinaFormUiState(
    val isEditMode: Boolean = false,
    val isLoadingRutina: Boolean = false,
    val loadError: String? = null,

    val nombre: String = "",
    val grupoMuscular: String = "",
    val series: String = "",
    val repeticiones: String = "",
    val activa: Boolean = true,

    val nombreError: String? = null,
    val grupoMuscularError: String? = null,
    val seriesError: String? = null,
    val repeticionesError: String? = null,

    val isSaving: Boolean = false,
    val message: String? = null,
    val isSaved: Boolean = false
)