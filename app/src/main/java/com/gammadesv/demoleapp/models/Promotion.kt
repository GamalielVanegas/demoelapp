package com.gammadesv.demoleapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val restaurantAddress: String = "",  // Nuevo campo para dirección
    val createdAt: Long = 0L
    //val restaurantId: String = "",  // Nuevo campo para referencia
    //val restaurantAddress: String = ""  // Nuevo campo para dirección
) : Parcelable