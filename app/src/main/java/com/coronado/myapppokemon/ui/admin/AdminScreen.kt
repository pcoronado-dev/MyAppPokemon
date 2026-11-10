package com.coronado.myapppokemon.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.components.Cargando
import com.coronado.myapppokemon.ui.components.ErrorView
import com.coronado.myapppokemon.ui.theme.coloresBarra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val ocupado by viewModel.ocupado.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Admin") },
                colors = coloresBarra(),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (val e = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = { viewModel.actualizarTotales() })
                is UiState.Exito -> {
                    TarjetaTotal("Pokémon guardados", e.datos.totalGuardado)
                    TarjetaTotal("Favoritos", e.datos.totalFavoritos)
                    Button(
                        onClick = { viewModel.sincronizar() },
                        enabled = !ocupado,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sincronizar") }
                    OutlinedButton(
                        onClick = { viewModel.limpiarCache() },
                        enabled = !ocupado,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Limpiar caché") }
                    mensaje?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaTotal(titulo: String, valor: Int) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text("$valor", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

