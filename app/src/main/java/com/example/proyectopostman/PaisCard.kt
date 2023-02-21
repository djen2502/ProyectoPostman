package com.example.proyectopostman

data class PaisResponse(val cards: List<PaisCard>)

data class PaisCard(
    //val capital: String,
    var name:String,
    val imageUrl: String

)