package com.coronado.myapppokemon.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.coronado.myapppokemon.domain.model.Item
import com.coronado.myapppokemon.ui.UiState
import com.coronado.myapppokemon.ui.components.Cargando
import com.coronado.myapppokemon.ui.components.ErrorView
import com.coronado.myapppokemon.ui.theme.RojoPokebola
import com.coronado.myapppokemon.ui.theme.coloresBarra
import com.coronado.myapppokemon.ui.theme.colorDeTipo
import com.coronado.myapppokemon.ui.theme.contenidoSobre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(viewModel: DetalleViewModel, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val item: Item? = when (val e = estado) {
        is UiState.Exito -> e.datos
        else -> null
    }
    // El color sale del primer tipo del Pokémon (dato del detalle de la API).
    // Mientras no hay detalle se usa el rojo de la app.
    val colorTipo = item?.tipos?.firstOrNull()?.let { colorDeTipo(it) } ?: MaterialTheme.colorScheme.primary
    val sobreColor = contenidoSobre(colorTipo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.titulo ?: "Detalle") },
                colors = coloresBarra(colorTipo),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (item != null) {
                        Surface(shape = CircleShape, color = Color.White, modifier = Modifier.padding(end = 8.dp)) {
                            IconButton(onClick = { viewModel.alternarFavorito() }) {
                                Icon(
                                    imageVector = if (item.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (item.esFavorito) RojoPokebola else Color(0xFF777777)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val e = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(e.mensaje, onReintentar = { viewModel.cargar() })
                is UiState.Exito -> ContenidoDetalle(e.datos, colorTipo, sobreColor)
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(item: Item, colorTipo: Color, sobreColor: Color) {
    val oscuro = lerp(colorTipo, Color.Black, 0.3f)

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(colorTipo, oscuro)))) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // Cabecera: nombre, número y sprite sobre un círculo claro
            Column(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    item.titulo,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = sobreColor
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        item.subtitulo,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF1B1B1F),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
                Box(Modifier.height(250.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(230.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.30f))
                    )
                    AsyncImage(
                        model = item.imagenUrl,
                        contentDescription = item.titulo,
                        modifier = Modifier.size(220.dp)
                    )
                }
            }

            // Panel blanco con los datos que entrega la API
            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().heightIn(min = 420.dp)
            ) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (item.tipos.isNotEmpty()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            item.tipos.forEach { tipo ->
                                val c = colorDeTipo(tipo)
                                Surface(shape = RoundedCornerShape(50), color = c) {
                                    Text(
                                        tipo,
                                        color = contenidoSobre(c),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (item.alturaMetros != null && item.pesoKg != null) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            DatoCaja("Peso", "${item.pesoKg} kg")
                            DatoCaja("Altura", "${item.alturaMetros} m")
                        }
                    }

                    if (item.habilidades.isNotEmpty()) {
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Habilidades", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(item.habilidades.joinToString(", "), style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    if (item.estadisticas.isNotEmpty()) {
                        Text("Estadísticas base", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        item.estadisticas.forEach { est ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(est.nombre, modifier = Modifier.width(130.dp))
                                LinearProgressIndicator(
                                    progress = { (est.valor / 255f).coerceIn(0f, 1f) },
                                    color = colorTipo,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(50))
                                )
                                Text("${est.valor}", modifier = Modifier.width(44.dp).padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoCaja(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(titulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
