package me.juangoncalves.mentra.network.models

import com.squareup.moshi.Json

data class ExchangeRatesSchema(
    @field:Json(name = "base")
    val base: String = "",
    @field:Json(name = "rates")
    val rates: Map<String, Double> = emptyMap()
)