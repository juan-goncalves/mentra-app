package me.juangoncalves.mentra.android_network.services.crypto_compare.models

import com.squareup.moshi.Json

data class PriceSchema(
    @field:Json(name = "USD")
    val USD: Double
)
