package com.gammadesv.demoleapp.models

data class Promotion(
    val id: String = "",
    val restaurantId: String = "",
    val title: String = "",
    val promotionType: String = "",
    val days: String = "",
    val hours: String = "",
    val price: String = "",
    val department: String = "",
    val foodType: String = "",
    val environment: String = "",
    val createdAt: Long = 0L
) {
    // Optional: You can add a secondary constructor if needed
    constructor() : this("", "", "", "", "", "")
}