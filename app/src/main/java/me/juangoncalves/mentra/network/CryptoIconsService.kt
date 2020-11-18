package me.juangoncalves.mentra.network

import retrofit2.Response
import retrofit2.http.HEAD
import retrofit2.http.Path

interface CryptoIconsService {

    @HEAD("/api/icon/{symbol}/200")
    suspend fun checkGradientIconAvailability(@Path("symbol") coinSymbol: String): Response<Void>

}