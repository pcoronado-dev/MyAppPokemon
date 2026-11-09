package com.coronado.myapppokemon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.coronado.myapppokemon.domain.model.Item
import com.coronado.myapppokemon.ui.theme.RojoPokebola
import com.coronado.myapppokemon.ui.theme.contenidoSobre

/**
 * Tarjeta con el estilo de Pokémon GO: degradado con el color del tipo,
 * un círculo claro detrás del sprite y el corazón de favorito.
 * Solo dibuja: los eventos se avisan con lambdas.
 */
@Composable
fun PokemonCard(
    item: Item,
    colorTipo: Color,
    onClick: () -> Unit,
    onFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    val oscuro = lerp(colorTipo, Color.Black, 0.25f)
    val texto = contenidoSobre(colorTipo)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).clickable(onClick = onClick)
    ) {
        Row(
            Modifier
                .background(Brush.horizontalGradient(listOf(colorTipo, oscuro)))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Círculo claro detrás del sprite
            Box(Modifier.size(92.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.35f))
                )
                AsyncImage(
                    model = item.imagenUrl,
                    contentDescription = item.titulo,
                    modifier = Modifier.size(84.dp)
                )
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    item.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = texto
                )
                Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.85f)) {
                    Text(
                        item.subtitulo,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF1B1B1F),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }

            Surface(shape = CircleShape, color = Color.White) {
                IconButton(onClick = onFavorito) {
                    Icon(
                        imageVector = if (item.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (item.esFavorito) "Quitar de favoritos" else "Marcar como favorito",
                        tint = if (item.esFavorito) RojoPokebola else Color(0xFF777777)
                    )
                }
            }
        }
    }
}
