package me.juangoncalves.mentra.android_network.services

import me.juangoncalves.mentra.android_network.models.ExchangeRatesSchema
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateService {

    @GET("/latest")
    suspend fun getExchangeRates(@Query("base") baseSymbol: String): ExchangeRatesSchema

}