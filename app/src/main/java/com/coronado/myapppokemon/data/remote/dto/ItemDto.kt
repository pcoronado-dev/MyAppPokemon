package com.coronado.myapppokemon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---- Endpoint 1: GET type/{tipo} ----
// { "pokemon": [ { "pokemon": { "name": "charmander", "url": ".../pokemon/4/" }, "slot": 1 } ] }
@Serializable
data class TipoRespuestaDto(val pokemon: List<PokemonEntradaDto> = emptyList())

@Serializable
data class PokemonEntradaDto(val pokemon: PokemonRefDto)

@Serializable
data class PokemonRefDto(val name: String, val url: String)

// ---- Endpoint 2: GET pokemon/{nombre} ----
@Serializable
data class PokemonDetalleDto(
    val id: Int,
    val name: String,
    val height: Int = 0, // decímetros
    val weight: Int = 0, // hectogramos
    val sprites: SpritesDto? = null,
    val types: List<TipoSlotDto> = emptyList(),
    val stats: List<StatSlotDto> = emptyList(),
    val abilities: List<HabilidadSlotDto> = emptyList()
)

@Serializable
data class SpritesDto(
    @SerialName("front_default") val frontDefault: String? = null,
    val other: OtrosSpritesDto? = null
)

@Serializable
data class OtrosSpritesDto(
    @SerialName("official-artwork") val officialArtwork: ArtworkDto? = null
)

@Serializable
data class ArtworkDto(@SerialName("front_default") val frontDefault: String? = null)

@Serializable
data class NombreDto(val name: String)

@Serializable
data class TipoSlotDto(val type: NombreDto)

@Serializable
data class StatSlotDto(@SerialName("base_stat") val baseStat: Int, val stat: NombreDto)

@Serializable
data class HabilidadSlotDto(val ability: NombreDto)
