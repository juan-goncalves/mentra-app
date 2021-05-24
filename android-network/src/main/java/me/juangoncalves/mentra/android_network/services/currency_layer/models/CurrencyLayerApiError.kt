package me.juangoncalves.mentra.android_network.services.currency_layer.models

import com.squareup.moshi.Json


data class CurrencyLayerApiError(
    @field:Json(name = "code")
    val code: Int,
    @field:Json(name = "info")
    val message: String,
)
