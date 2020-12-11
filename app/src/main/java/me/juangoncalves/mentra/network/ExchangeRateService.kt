package me.juangoncalves.mentra.network

import me.juangoncalves.mentra.network.models.ExchangeRatesSchema
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateService {

    @GET("/latest")
    suspend fun getExchangeRates(@Query("base") baseSymbol: String): Response<ExchangeRatesSchema>

}