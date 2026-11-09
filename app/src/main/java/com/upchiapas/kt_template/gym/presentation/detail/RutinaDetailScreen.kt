package com.upchiapas.kt_template.gym.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upchiapas.kt_template.core.ui.CenteredMessage
import com.upchiapas.kt_template.gym.domain.model.Rutina

@Composable
fun RutinaDetailScreen(
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: RutinaDetailViewModel = viewModel(factory = RutinaDetailViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Al volver del formulario de edición, el detalle se refresca solo
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRutina()
    }

    RutinaDetailContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onEditClick = onEditClick,
        onRetry = viewModel::loadRutina
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RutinaDetailContent(
    state: RutinaDetailUiState,
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de rutina") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state is RutinaDetailUiState.Success) {
                        IconButton(onClick = { onEditClick(state.rutina.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar rutina")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                RutinaDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is RutinaDetailUiState.Error -> {
                    CenteredMessage(
                        text = state.message,
                        actionLabel = "Reintentar",
                        onAction = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RutinaDetailUiState.Success -> DetailBody(state.rutina)
            }
        }
    }
}

@Composable
private fun DetailBody(rutina: Rutina) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailRow(label = "ID", value = rutina.id.toString())
                DetailRow(label = "Nombre", value = rutina.nombre)
                DetailRow(label = "Grupo muscular", value = rutina.grupoMuscular)
                DetailRow(label = "Series", value = rutina.series.toString())
                DetailRow(label = "Repeticiones por serie", value = rutina.repeticiones.toString())
                DetailRow(label = "Estado", value = if (rutina.activa) "Activa" else "Inactiva")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}