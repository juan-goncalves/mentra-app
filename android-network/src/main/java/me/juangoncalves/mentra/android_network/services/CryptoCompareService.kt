package me.juangoncalves.mentra.android_network.services

import me.juangoncalves.mentra.android_network.models.CoinListSchema
import me.juangoncalves.mentra.android_network.models.PriceSchema
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoCompareService {

    @GET("/data/all/coinlist")
    suspend fun listCoins(): Response<CoinListSchema>

    @GET("/data/price")
    suspend fun getCoinPrice(
        @Query("fsym") symbol: String,
        @Query("tsyms") conversionSymbols: String = "USD"
    ): Response<PriceSchema>

}