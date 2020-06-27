package me.juangoncalves.mentra.features.portfolio.data.schemas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinSchema(
    @SerialName("Id")
    val id: String = "",
    @SerialName("Symbol")
    val symbol: String = "",
    @SerialName("ImageUrl")
    val imageUrl: String = "",
    @SerialName("CoinName")
    val name: String = "",
    @SerialName("Sponsored")
    val sponsored: Boolean = false,
    @SerialName("SortOrder")
    val sortPosition: String = "${Int.MAX_VALUE}"
)
