package me.juangoncalves.mentra.features.portfolio.data.schemas

import com.squareup.moshi.Json


data class CoinListSchema(
    @field:Json(name = "BaseImageUrl")
    val baseImageUrl: String = "",
    @field:Json(name = "BaseLinkUrl")
    val baseLinkUrl: String = "",
    @field:Json(name = "Data")
    val data: Map<String, CoinSchema> = mapOf()
)