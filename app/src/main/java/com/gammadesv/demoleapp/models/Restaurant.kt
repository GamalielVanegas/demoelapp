package com.gammadesv.demoleapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val mapUrl: String = "",
    val manager: String = "",
    val email: String = "",
    val website: String = "",
    val facebook: String = "",
    val instagram: String = ""
) : Parcelable