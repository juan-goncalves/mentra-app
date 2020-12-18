package me.juangoncalves.mentra.android_cache.error

import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import javax.inject.Inject

class RoomErrorHandler @Inject constructor() : ErrorHandler {

    override fun getFailure(throwable: Throwable): Failure {
        return when (throwable) {
            else -> Failure.Unknown
        }
    }

}