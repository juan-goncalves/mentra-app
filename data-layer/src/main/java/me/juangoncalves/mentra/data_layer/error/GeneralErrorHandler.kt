package me.juangoncalves.mentra.data_layer.error

import me.juangoncalves.mentra.data_layer.di.LocalErrorHandler
import me.juangoncalves.mentra.data_layer.di.NetworkErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.TAG
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import javax.inject.Inject

// If we migrate the network and database libraries to use pure kotlin alternatives (Ktor & SQLDelight)
// we can process every exception directly without in the `getFailure` method
class GeneralErrorHandler @Inject constructor(
    private val logger: MentraLogger,
    @NetworkErrorHandler private val networkErrorHandler: ErrorHandler,
    @LocalErrorHandler private val localErrorHandler: ErrorHandler
) : ErrorHandler {

    override fun getFailure(throwable: Throwable): Failure {
        val networkFailure = networkErrorHandler.getFailure(throwable)

        return when {
            networkFailure != Failure.Unknown -> networkFailure
            else -> localErrorHandler.getFailure(throwable)
        }.also { logger.error(TAG, "Exception caught ($it): $throwable") }
    }

}