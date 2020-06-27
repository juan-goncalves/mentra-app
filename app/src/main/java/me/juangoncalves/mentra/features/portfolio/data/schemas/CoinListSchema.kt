package me.juangoncalves.mentra.features.portfolio.data.schemas

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinListSchema(
    @SerialName("BaseImageUrl")
    val baseImageUrl: String = "",
    @SerialName("BaseLinkUrl")
    val baseLinkUrl: String = "",
    @SerialName("Data")
    val data: Map<String, CoinSchema> = mapOf()
)