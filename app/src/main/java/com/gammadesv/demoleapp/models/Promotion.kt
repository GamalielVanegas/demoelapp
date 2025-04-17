package com.gammadesv.demoleapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Promotion(
    val id: String = "",
    val restaurantId: String = "",
    var restaurantName: String = "",  // Cambiado a var
    var restaurantAddress: String = "", // Cambiado a var
    val title: String = "",
    val promotionType: String = "",
    val days: String = "",
    val hours: String = "",
    val price: String = "",
    val department: String = "",
    val foodType: String = "",
    val environment: String = "",
    var mapUrl: String = "", // Cambiado a var
    val createdAt: Long = 0L
) : Parcelable