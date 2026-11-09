package com.coronado.myapppokemon.data.mapper

import com.coronado.myapppokemon.data.local.ItemEntity
import com.coronado.myapppokemon.data.remote.dto.PokemonDetalleDto
import com.coronado.myapppokemon.data.remote.dto.PokemonEntradaDto
import com.coronado.myapppokemon.domain.model.Estadistica
import com.coronado.myapppokemon.domain.model.Item

private const val SPRITES = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

/** "special-attack" -> "Special Attack" */
fun String.formatearNombre(): String =
    split("-").joinToString(" ") { palabra -> palabra.replaceFirstChar { it.uppercase() } }

// Dto (API) -> Entity (Room)
fun PokemonEntradaDto.aEntity(tipo: String, esFavorito: Boolean = false): ItemEntity {
    val numero = pokemon.url.trimEnd('/').substringAfterLast('/')
    return ItemEntity(
        id = pokemon.name,
        titulo = pokemon.name.formatearNombre(),
        subtitulo = "#$numero",
        imagenUrl = "$SPRITES$numero.png",
        esFavorito = esFavorito,
        tipoPrincipal = tipo
    )
}

// Entity (Room) -> modelo (UI)
fun ItemEntity.aModelo(): Item = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    imagenUrl = imagenUrl,
    esFavorito = esFavorito,
    tipos = if (tipoPrincipal.isBlank()) emptyList() else listOf(tipoPrincipal.formatearNombre())
)

// Dto del detalle (API) -> modelo (UI)
fun PokemonDetalleDto.aModelo(esFavorito: Boolean): Item = Item(
    id = name,
    titulo = name.formatearNombre(),
    subtitulo = "#$id",
    imagenUrl = sprites?.other?.officialArtwork?.frontDefault ?: sprites?.frontDefault,
    esFavorito = esFavorito,
    tipos = types.map { it.type.name.formatearNombre() },
    alturaMetros = height / 10.0,
    pesoKg = weight / 10.0,
    estadisticas = stats.map { Estadistica(it.stat.name.formatearNombre(), it.baseStat) },
    habilidades = abilities.map { it.ability.name.formatearNombre() }
)
