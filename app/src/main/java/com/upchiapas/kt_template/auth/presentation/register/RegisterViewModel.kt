package com.upchiapas.kt_template.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.kt_template.KtApplication
import com.upchiapas.kt_template.core.di.AppContainer
import com.upchiapas.kt_template.auth.domain.model.AuthException
import com.upchiapas.kt_template.auth.domain.repository.AuthRepository
import com.upchiapas.kt_template.auth.domain.validation.AuthValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onRegisterClick() {
        val current = _uiState.value
        if (current.isLoading) return

        val nameError = if (current.name.isBlank()) "El nombre es obligatorio" else null
        val emailError = AuthValidator.validateEmail(current.email)
        val passwordError = AuthValidator.validateNewPassword(current.password)
        val confirmError = when {
            current.confirmPassword.isBlank() -> "Confirma tu contraseña"
            current.confirmPassword != current.password -> "Las contraseñas no coinciden"
            else -> null
        }

        if (listOf(nameError, emailError, passwordError, confirmError).any { it != null }) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.register(
                    name = current.name.trim(),
                    email = current.email.trim(),
                    password = current.password
                )
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = if (e is AuthException) e.message else null
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message ?: "Ocurrió un error inesperado"
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KtApplication
                RegisterViewModel(repository = application.container.authRepository)
            }
        }
    }
}