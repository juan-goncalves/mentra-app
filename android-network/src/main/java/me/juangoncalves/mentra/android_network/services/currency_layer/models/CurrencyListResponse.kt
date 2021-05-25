package me.juangoncalves.mentra.android_network.services.currency_layer.models

import com.squareup.moshi.Json

data class CurrencyListResponse(
    @field:Json(name = "success")
    val wasSuccessful: Boolean,

    @field:Json(name = "error")
    val error: CurrencyLayerApiError?,

    @field:Json(name = "currencies")
    val currencies: Map<String, String> = emptyMap(),
)