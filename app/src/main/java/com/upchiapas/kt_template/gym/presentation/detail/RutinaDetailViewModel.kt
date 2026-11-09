package com.upchiapas.kt_template.gym.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.kt_template.KtApplication
import com.upchiapas.kt_template.core.navigation.Routes
import com.upchiapas.kt_template.gym.domain.model.RutinasException
import com.upchiapas.kt_template.gym.domain.repository.RutinasRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RutinaDetailViewModel(
    private val rutinaId: Int,
    private val repository: RutinasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RutinaDetailUiState>(RutinaDetailUiState.Loading)
    val uiState: StateFlow<RutinaDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadRutina() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            // Si ya hay datos en pantalla, se refrescan sin mostrar "Cargando"
            if (_uiState.value !is RutinaDetailUiState.Success) {
                _uiState.value = RutinaDetailUiState.Loading
            }
            try {
                _uiState.value = RutinaDetailUiState.Success(repository.getRutina(rutinaId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = RutinaDetailUiState.Error(
                    (e as? RutinasException)?.message ?: "Ocurrió un error inesperado"
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KtApplication
                // Navigation guarda los argumentos de la ruta en el SavedStateHandle
                val savedStateHandle = createSavedStateHandle()
                RutinaDetailViewModel(
                    rutinaId = checkNotNull(savedStateHandle[Routes.RUTINA_ID]),
                    repository = application.container.rutinasRepository
                )
            }
        }
    }
}