package com.coronado.myapppokemon.data.remote

import com.coronado.myapppokemon.data.remote.dto.PokemonDetalleDto
import com.coronado.myapppokemon.data.remote.dto.TipoRespuestaDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    /** Endpoint 1 (lista): https://pokeapi.co/api/v2/type/{tipo} */
    @GET("type/{tipo}")
    suspend fun listar(@Path("tipo") tipo: String): TipoRespuestaDto

    /** Endpoint 2 (detalle): https://pokeapi.co/api/v2/pokemon/{nombre} */
    @GET("pokemon/{nombre}")
    suspend fun detalle(@Path("nombre") nombre: String): PokemonDetalleDto
}