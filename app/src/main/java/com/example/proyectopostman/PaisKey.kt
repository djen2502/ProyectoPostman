package com.example.proyectopostman

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PaisKey {
    @GET("cards")
    fun getCards(
        @Query("d7t8frAA0E9DUTPZzYKNtNZNXiH1wcR1") apiKey: String,
        @Query("page") page: Int
    ): Call<PaisResponse>

}