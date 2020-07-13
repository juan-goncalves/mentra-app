package me.juangoncalves.mentra.network.models

import com.squareup.moshi.Json

data class PriceSchema(
    @field:Json(name = "USD")
    val USD: Double
)
