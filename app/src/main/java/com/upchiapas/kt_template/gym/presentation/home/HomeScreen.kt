package com.upchiapas.kt_template.gym.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun HomeScreen(
    onRutinaClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Cada vez que la pantalla vuelve a primer plano se refresca la lista
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRutinas()
    }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLoggedOut()
    }

    HomeContent(
        state = state,
        onRutinaClick = onRutinaClick,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onRetry = viewModel::loadRutinas,
        onDeleteClick = viewModel::onDeleteClick,
        onDeleteConfirm = viewModel::onDeleteConfirm,
        onDeleteDismiss = viewModel::onDeleteDismiss,
        onMessageShown = viewModel::onMessageShown,
        onLogoutClick = viewModel::onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState,
    onRutinaClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onDeleteClick: (Rutina) -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    onMessageShown: () -> Unit,
    onLogoutClick: () -> Unit
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
                title = { Text("Mis rutinas") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Crear rutina")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val list = state.list) {
                RutinasListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                RutinasListState.Empty -> {
                    CenteredMessage(
                        text = "Aún no tienes rutinas.\nToca + para crear la primera.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RutinasListState.Error -> {
                    CenteredMessage(
                        text = list.message,
                        actionLabel = "Reintentar",
                        onAction = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RutinasListState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(list.rutinas, key = { it.id }) { rutina ->
                            RutinaItem(
                                rutina = rutina,
                                onClick = { onRutinaClick(rutina.id) },
                                onEdit = { onEditClick(rutina.id) },
                                onDelete = { onDeleteClick(rutina) }
                            )
                        }
                    }
                }
            }
        }
    }

    state.rutinaToDelete?.let { rutina ->
        AlertDialog(
            onDismissRequest = onDeleteDismiss,
            title = { Text("Eliminar rutina") },
            text = {
                Text("¿Seguro que quieres eliminar \"${rutina.nombre}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = onDeleteConfirm) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = onDeleteDismiss) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun RutinaItem(
    rutina: Rutina,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = rutina.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = rutina.grupoMuscular,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${rutina.series} series × ${rutina.repeticiones} repeticiones",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = if (rutina.activa) "Activa" else "Inactiva",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar rutina")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar rutina")
            }
        }
    }
}

