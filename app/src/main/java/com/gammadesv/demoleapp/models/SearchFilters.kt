package com.gammadesv.demoleapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchFilters(
    val department: String = "",
    val foodType: String = "",
    val promotionType: String = "",
    val environment: String = ""
) : Parcelable