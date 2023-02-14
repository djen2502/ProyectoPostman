package com.example.proyectopostman

data class PaisResponse(val cards: List<PaisCard>)

data class PaisCard(
    val name: String,
    val imageUrl: String,
    var slogan:String
)