package me.juangoncalves.mentra.android_network.services.currency_layer.models

import com.squareup.moshi.Json

data class ExchangeRatesResponse(
    @field:Json(name = "success")
    val wasSuccessful: Boolean,

    @field:Json(name = "error")
    val error: CurrencyLayerApiError?,

    @field:Json(name = "timestamp")
    val timestamp: Long,

    @field:Json(name = "quotes")
    val quotes: Map<String, Double> = emptyMap(),
)