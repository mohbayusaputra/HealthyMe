package com.example.healthyme.Data.Model

data class Nutrition(
    val id: Int = 0,
    val calories: Float = 0f,
    val proteins: Float = 0f,
    val fat: Float = 0f,
    val carbohydrate: Float = 0f,
    val name: String = "",
    val image: String = "",
    val cluster: Int = 0,
    var firebaseKey: String? = null
)
