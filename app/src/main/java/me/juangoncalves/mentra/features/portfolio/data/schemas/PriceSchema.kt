package me.juangoncalves.mentra.features.portfolio.data.schemas

import com.squareup.moshi.Json

data class PriceSchema(
    @field:Json(name = "USD")
    val USD: Double
)
