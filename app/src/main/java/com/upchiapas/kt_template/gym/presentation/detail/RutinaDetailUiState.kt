package com.upchiapas.kt_template.gym.presentation.detail

import com.upchiapas.kt_template.gym.domain.model.Rutina

sealed interface RutinaDetailUiState {
    data object Loading : RutinaDetailUiState
    data class Success(val rutina: Rutina) : RutinaDetailUiState
    data class Error(val message: String) : RutinaDetailUiState
}