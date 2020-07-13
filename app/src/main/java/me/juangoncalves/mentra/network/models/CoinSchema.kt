package me.juangoncalves.mentra.network.models

import com.squareup.moshi.Json

data class CoinSchema(
    @field:Json(name = "Id")
    val id: String = "",
    @field:Json(name = "Symbol")
    val symbol: String = "",
    @field:Json(name = "ImageUrl")
    val imageUrl: String = "",
    @field:Json(name = "CoinName")
    val name: String = "",
    @field:Json(name = "Sponsored")
    val sponsored: Boolean = false,
    @field:Json(name = "SortOrder")
    val sortPosition: String = "${Int.MAX_VALUE}"
)
