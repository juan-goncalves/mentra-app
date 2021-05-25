package me.juangoncalves.mentra.android_network.services.crypto_icons

import retrofit2.Response
import retrofit2.http.HEAD
import retrofit2.http.Path

interface CryptoIconsService {

    @HEAD("/api/icon/{symbol}/200")
    suspend fun checkGradientIconAvailability(@Path("symbol") coinSymbol: String): Response<Void>

}