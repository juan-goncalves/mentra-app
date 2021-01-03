package me.juangoncalves.mentra.android_network.error

import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject

class RetrofitErrorHandler @Inject constructor() : ErrorHandler {

    private companion object {
        const val UNSATISFIABLE_REQUEST = 504
    }

    override fun getFailure(throwable: Throwable): Failure {
        return when (throwable) {
            is IOException -> Failure.Network
            is CryptoCompareResponseException -> Failure.ServiceUnavailable
            is HttpException -> {
                when (throwable.code()) {
                    // No cache found in case of no network, thrown by retrofit
                    UNSATISFIABLE_REQUEST -> Failure.Network
                    HttpURLConnection.HTTP_NOT_FOUND -> Failure.NotFound
                    HttpURLConnection.HTTP_FORBIDDEN -> Failure.AccessDenied
                    HttpURLConnection.HTTP_UNAVAILABLE -> Failure.ServiceUnavailable
                    else -> Failure.Unknown
                }
            }
            else -> Failure.Unknown
        }
    }

}