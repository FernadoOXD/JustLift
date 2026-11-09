package com.upchiapas.kt_template.gym.presentation.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upchiapas.kt_template.core.ui.CenteredMessage

@Composable
fun RutinaFormScreen(
    onSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RutinaFormViewModel = viewModel(factory = RutinaFormViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    RutinaFormContent(
        state = state,
        onNombreChange = viewModel::onNombreChange,
        onGrupoMuscularChange = viewModel::onGrupoMuscularChange,
        onSeriesChange = viewModel::onSeriesChange,
        onRepeticionesChange = viewModel::onRepeticionesChange,
        onActivaChange = viewModel::onActivaChange,
        onSaveClick = viewModel::onSaveClick,
        onRetryLoad = viewModel::loadRutina,
        onMessageShown = viewModel::onMessageShown,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RutinaFormContent(
    state: RutinaFormUiState,
    onNombreChange: (String) -> Unit,
    onGrupoMuscularChange: (String) -> Unit,
    onSeriesChange: (String) -> Unit,
    onRepeticionesChange: (String) -> Unit,
    onActivaChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onRetryLoad: () -> Unit,
    onMessageShown: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar rutina" else "Nueva rutina") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoadingRutina -> {
                Box(Modifier
                    .fillMaxSize()
                    .padding(padding)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            state.loadError != null -> {
                Box(Modifier
                    .fillMaxSize()
                    .padding(padding)) {
                    CenteredMessage(
                        text = state.loadError,
                        actionLabel = "Reintentar",
                        onAction = onRetryLoad,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = onNombreChange,
                        label = { Text("Nombre de la rutina") },
                        isError = state.nombreError != null,
                        supportingText = { state.nombreError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.grupoMuscular,
                        onValueChange = onGrupoMuscularChange,
                        label = { Text("Grupo muscular") },
                        isError = state.grupoMuscularError != null,
                        supportingText = { state.grupoMuscularError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.series,
                        onValueChange = onSeriesChange,
                        label = { Text("Series (1 a 20)") },
                        isError = state.seriesError != null,
                        supportingText = { state.seriesError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.repeticiones,
                        onValueChange = onRepeticionesChange,
                        label = { Text("Repeticiones por serie (1 a 100)") },
                        isError = state.repeticionesError != null,
                        supportingText = { state.repeticionesError?.let { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rutina activa", style = MaterialTheme.typography.bodyLarge)
                        Switch(checked = state.activa, onCheckedChange = onActivaChange)
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onSaveClick,
                        enabled = !state.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (state.isEditMode) "Guardar cambios" else "Crear rutina")
                        }
                    }
                }
            }
        }
    }
}