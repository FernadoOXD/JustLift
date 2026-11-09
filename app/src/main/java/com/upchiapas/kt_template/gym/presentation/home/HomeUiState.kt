package com.upchiapas.kt_template.gym.presentation.home

import com.upchiapas.kt_template.gym.domain.model.Rutina

sealed interface RutinasListState {
    data object Loading : RutinasListState
    data object Empty : RutinasListState
    data class Success(val rutinas: List<Rutina>) : RutinasListState
    data class Error(val message: String) : RutinasListState
}

data class HomeUiState(
    val list: RutinasListState = RutinasListState.Loading,
    val rutinaToDelete: Rutina? = null,
    val message: String? = null,
    val isLoggedOut: Boolean = false
)