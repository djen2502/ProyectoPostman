package com.example.proyectopostman

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PaisKey {
    @GET("all")
    fun getCards(
        @Query("page") page: Int,
        @Query("fields") capital: String
    ): Call<PaisResponse>

}