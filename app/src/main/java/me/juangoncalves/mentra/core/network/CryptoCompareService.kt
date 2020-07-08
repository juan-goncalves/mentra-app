package me.juangoncalves.mentra.core.network

import me.juangoncalves.mentra.core.network.schemas.CoinListSchema
import me.juangoncalves.mentra.core.network.schemas.PriceSchema
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