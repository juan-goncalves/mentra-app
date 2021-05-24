package me.juangoncalves.mentra.android_network.services.crypto_compare

import me.juangoncalves.mentra.android_network.services.crypto_compare.models.CoinListSchema
import me.juangoncalves.mentra.android_network.services.crypto_compare.models.PriceSchema
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoCompareApi {

    @GET("/data/all/coinlist")
    suspend fun listCoins(): CoinListSchema

    @GET("/data/price")
    suspend fun getCoinPrice(
        @Query("fsym") symbol: String,
        @Query("tsyms") conversionSymbols: String = "USD"
    ): PriceSchema

}