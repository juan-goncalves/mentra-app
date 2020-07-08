package me.juangoncalves.mentra.core.network.schemas

import com.squareup.moshi.Json

data class PriceSchema(
    @field:Json(name = "USD")
    val USD: Double
)
