package com.example.proyectomodificaciones

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface SamirService {

    @GET("inmuebles/")
    fun getInmuebles(): Call<List<SamirResponse>>

    @POST("inmuebles/")
    fun postMyData(@Body data: SamirResponse): Call<SamirResponse>

    @DELETE("inmuebles/")
    fun deleteInmuebleByTitle(@Query("idInmueble") idInmueble: Int): Call<Void>

    @DELETE
    fun deleteInmueble(@Url url:String): Call<Void>

    @POST
    fun putInmueble(@Url url:String, @Body samirClass: SamirResponse): Call<SamirResponse>


}

