package me.juangoncalves.mentra.features.portfolio.data.schemas

import com.squareup.moshi.Json

data class CryptoCompareResponse<T>(
    @field:Json(name = "Response")
    val status: State = State.Error,
    @field:Json(name = "Message")
    val message: String = "",
    @field:Json(name = "BaseImageUrl")
    val baseImageUrl: String = "",
    @field:Json(name = "BaseLinkUrl")
    val baseLinkUrl: String = "",
    @field:Json(name = "Data")
    val data: T
) {
    enum class State {
        @Json(name = "Success") Success,
        @Json(name = "Error") Error,
    }
}

