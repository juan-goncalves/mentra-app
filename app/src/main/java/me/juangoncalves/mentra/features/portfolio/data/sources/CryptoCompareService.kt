package me.juangoncalves.mentra.features.portfolio.data.sources

import me.juangoncalves.mentra.features.portfolio.data.schemas.CoinListSchema
import retrofit2.Response
import retrofit2.http.GET

// TODO: Move to a package inside `core`
interface CryptoCompareService {

    @GET("/data/all/coinlist")
    suspend fun listCoins(): Response<CoinListSchema>

}