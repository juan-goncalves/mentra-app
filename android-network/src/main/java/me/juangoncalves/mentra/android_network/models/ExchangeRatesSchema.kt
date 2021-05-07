package me.juangoncalves.mentra.android_network.models

import com.squareup.moshi.Json

data class ExchangeRatesSchema(
    @field:Json(name = "success")
    val wasSuccessful: Boolean,
    @field:Json(name = "base")
    val base: String = "",
    @field:Json(name = "rates")
    val rates: Map<String, Double> = emptyMap(),
    @field:Json(name = "error")
    val error: Error = Error.None,
) {
    data class Error(
        @field:Json(name = "code")
        val code: Int,
        @field:Json(name = "info")
        val message: String,
    ) {
        companion object {
            val None = Error(-1, "None")
        }
    }
}