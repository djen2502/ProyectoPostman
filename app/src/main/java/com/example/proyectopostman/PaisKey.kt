package com.example.proyectopostman

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PaisKey {
    @GET("v1/cards")
    fun getCards(
    ): Call<PaisResponse>

    @GET("v1/cards")
    fun getCarta(
        @Query("name") name: String
    ): Call<PaisResponse>


    }

