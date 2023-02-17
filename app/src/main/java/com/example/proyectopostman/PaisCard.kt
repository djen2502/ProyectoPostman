package com.example.proyectopostman

data class PaisResponse(val cards: List<PaisCard>)

data class PaisCard(
    val capital: String,
    //val imageUrl: String,
    var name:String
)