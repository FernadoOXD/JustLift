package com.upchiapas.kt_template.gym.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.kt_template.KtApplication
import com.upchiapas.kt_template.core.storage.AuthPreferences
import com.upchiapas.kt_template.gym.domain.model.Rutina
import com.upchiapas.kt_template.gym.domain.model.RutinasException
import com.upchiapas.kt_template.gym.domain.repository.RutinasRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: RutinasRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadRutinas() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (_uiState.value.list !is RutinasListState.Success) {
                _uiState.update { it.copy(list = RutinasListState.Loading) }
            }
            try {
                val rutinas = repository.getRutinas()
                val newList = if (rutinas.isEmpty()) {
                    RutinasListState.Empty
                } else {
                    RutinasListState.Success(rutinas)
                }
                _uiState.update { it.copy(list = newList) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(list = RutinasListState.Error(e.userMessage())) }
            }
        }
    }

    fun onDeleteClick(rutina: Rutina) {
        _uiState.update { it.copy(rutinaToDelete = rutina) }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(rutinaToDelete = null) }
    }

    fun onDeleteConfirm() {
        val rutina = _uiState.value.rutinaToDelete ?: return
        _uiState.update { it.copy(rutinaToDelete = null) }

        viewModelScope.launch {
            try {
                repository.deleteRutina(rutina.id)
                _uiState.update { it.copy(message = "Rutina eliminada") }
                loadRutinas()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    fun onLogoutClick() {
        authPreferences.clearSession()
        _uiState.update { it.copy(isLoggedOut = true) }
    }

    private fun Exception.userMessage(): String =
        (this as? RutinasException)?.message ?: "Ocurrió un error inesperado"

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KtApplication
                HomeViewModel(
                    repository = application.container.rutinasRepository,
                    authPreferences = application.container.authPreferences
                )
            }
        }
    }
}