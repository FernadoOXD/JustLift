package com.upchiapas.kt_template.gym.presentation.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.kt_template.KtApplication
import com.upchiapas.kt_template.core.navigation.Routes
import com.upchiapas.kt_template.gym.domain.model.Rutina
import com.upchiapas.kt_template.gym.domain.model.RutinasException
import com.upchiapas.kt_template.gym.domain.repository.RutinasRepository
import com.upchiapas.kt_template.gym.domain.validation.RutinaValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RutinaFormViewModel(
    private val rutinaId: Int?, // null = modo creación
    private val repository: RutinasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RutinaFormUiState(
            isEditMode = rutinaId != null,
            isLoadingRutina = rutinaId != null
        )
    )
    val uiState: StateFlow<RutinaFormUiState> = _uiState.asStateFlow()

    init {
        loadRutina()
    }

    /** Solo en modo edición: trae la rutina y precarga los campos. También sirve para reintentar. */
    fun loadRutina() {
        val id = rutinaId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRutina = true, loadError = null) }
            try {
                val rutina = repository.getRutina(id)
                _uiState.update {
                    it.copy(
                        isLoadingRutina = false,
                        nombre = rutina.nombre,
                        grupoMuscular = rutina.grupoMuscular,
                        series = rutina.series.toString(),
                        repeticiones = rutina.repeticiones.toString(),
                        activa = rutina.activa
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingRutina = false, loadError = e.userMessage()) }
            }
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, nombreError = null) }
    }

    fun onGrupoMuscularChange(value: String) {
        _uiState.update { it.copy(grupoMuscular = value, grupoMuscularError = null) }
    }

    // Solo se aceptan dígitos (máximo 3), así no se puede escribir texto en campos numéricos
    fun onSeriesChange(value: String) {
        _uiState.update {
            it.copy(series = value.filter(Char::isDigit).take(3), seriesError = null)
        }
    }

    fun onRepeticionesChange(value: String) {
        _uiState.update {
            it.copy(repeticiones = value.filter(Char::isDigit).take(3), repeticionesError = null)
        }
    }

    fun onActivaChange(value: Boolean) {
        _uiState.update { it.copy(activa = value) }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    fun onSaveClick() {
        val current = _uiState.value
        if (current.isSaving) return

        val nombreError = RutinaValidator.validateNombre(current.nombre)
        val grupoError = RutinaValidator.validateGrupoMuscular(current.grupoMuscular)
        val seriesError = RutinaValidator.validateSeries(current.series)
        val repeticionesError = RutinaValidator.validateRepeticiones(current.repeticiones)

        if (listOf(nombreError, grupoError, seriesError, repeticionesError).any { it != null }) {
            _uiState.update {
                it.copy(
                    nombreError = nombreError,
                    grupoMuscularError = grupoError,
                    seriesError = seriesError,
                    repeticionesError = repeticionesError
                )
            }
            return
        }

        // Si llegamos aquí, los números ya fueron validados, toInt() es seguro
        val rutina = Rutina(
            id = rutinaId ?: 0,
            nombre = current.nombre.trim(),
            grupoMuscular = current.grupoMuscular.trim(),
            series = current.series.toInt(),
            repeticiones = current.repeticiones.toInt(),
            activa = current.activa
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                if (rutinaId == null) {
                    repository.createRutina(rutina)
                } else {
                    repository.updateRutina(rutina)
                }
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, message = e.userMessage()) }
            }
        }
    }

    private fun Exception.userMessage(): String =
        (this as? RutinasException)?.message ?: "Ocurrió un error inesperado"

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KtApplication
                val savedStateHandle = createSavedStateHandle()
                val id: Int = savedStateHandle[Routes.RUTINA_ID] ?: Routes.NO_ID
                RutinaFormViewModel(
                    rutinaId = id.takeIf { it != Routes.NO_ID },
                    repository = application.container.rutinasRepository
                )
            }
        }
    }
}