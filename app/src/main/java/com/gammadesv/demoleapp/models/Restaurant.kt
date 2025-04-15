package com.gammadesv.demoleapp.models

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val department: String = "",
    val foodType: String = "",
    val promotionType: String = "",
    val environment: String = "",
    val promotionTitle: String = "",
    val promotionPrice: String = "",
    val mapUrl: String = ""
)

data class SearchFilters(
    val department: String = "",
    val foodType: String = "",
    val promotionType: String = "",
    val environment: String = ""
)