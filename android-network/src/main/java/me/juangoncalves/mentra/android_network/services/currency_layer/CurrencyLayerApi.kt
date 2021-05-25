package me.juangoncalves.mentra.android_network.services.currency_layer

import me.juangoncalves.mentra.android_network.BuildConfig
import me.juangoncalves.mentra.android_network.services.currency_layer.models.CurrencyListResponse
import me.juangoncalves.mentra.android_network.services.currency_layer.models.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyLayerApi {

    @GET("/live")
    suspend fun getExchangeRates(
        @Query("source") baseCurrencyCode: String,
        @Query("access_key") accessKey: String = BuildConfig.CurrencyLayerApiKey,
    ): ExchangeRatesResponse

    @GET("/list")
    suspend fun getCurrencies(
        @Query("access_key") accessKey: String = BuildConfig.CurrencyLayerApiKey,
    ): CurrencyListResponse

}