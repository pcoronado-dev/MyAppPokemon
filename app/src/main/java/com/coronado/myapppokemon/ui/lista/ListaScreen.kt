package com.coronado.myapppokemon.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coronado.myapppokemon.domain.ConfigTema
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.components.Cargando
import com.coronado.myapppokemon.ui.components.ErrorView
import com.coronado.myapppokemon.ui.components.PokemonCard
import com.coronado.myapppokemon.ui.theme.colorDeTipo
import com.coronado.myapppokemon.ui.theme.coloresBarra
import com.coronado.myapppokemon.ui.theme.contenidoSobre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    esAdmin: Boolean,
    onItemClick: (String) -> Unit,
    onFavoritos: () -> Unit,
    onAdmin: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    val tipo by viewModel.tipo.collectAsStateWithLifecycle()
    val colorTipo = colorDeTipo(tipo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokédex") },
                colors = coloresBarra(),
                actions = {
                    IconButton(onClick = { viewModel.refrescar() }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Sincronizar Pokédex")
                    }
                    TextButton(
                        onClick = onCerrarSesion,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) { Text("Salir") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ConfigTema.TIPOS, key = { it }) { t ->
                    val color = colorDeTipo(t)
                    FilterChip(
                        selected = t == tipo,
                        onClick = { viewModel.seleccionarTipo(t) },
                        label = { Text(t.replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color,
                            selectedLabelColor = contenidoSobre(color)
                        ),
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onFavoritos,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) { Text("Favoritos") }
                if (esAdmin) {
                    OutlinedButton(
                        onClick = onAdmin,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) { Text("Panel Admin") }
                }
            }

            aviso?.let {
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            when (val e = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = { viewModel.refrescar() })
                is UiState.Exito -> {
                    if (e.datos.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "No hay Pokémon guardados para este tipo",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(
                                    onClick = { viewModel.refrescar() },
                                    modifier = Modifier.padding(top = 16.dp).heightIn(min = 48.dp)
                                ) { Text("Sincronizar ahora") }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(e.datos, key = { it.id }) { item ->
                                PokemonCard(
                                    item = item,
                                    colorTipo = colorTipo,
                                    onClick = { onItemClick(item.id) },
                                    onFavorito = { viewModel.alternarFavorito(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
