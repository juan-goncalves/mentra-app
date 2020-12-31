package me.juangoncalves.mentra.domain_layer.errors

import either.Either
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import kotlin.coroutines.CoroutineContext

interface ErrorHandler {

    /**
     * Handles the processing of the [Throwable] (side effects, logging, etc) and returns the
     * corresponding [Failure].
     * */
    fun getFailure(throwable: Throwable): Failure

}

/**
 * Executes the [block] handling any [Exception]s that are thrown except [CancellationException]
 * to avoid interrupting the coroutine cancellation chain, wrapping the result in a [Either].
 *
 * See [ErrorHandler.ignoringFailure] which supplements this method.
 *
 * @return [Either] with the result of the [block] or a [Failure]
 * */
suspend inline fun <T> ErrorHandler.runCatching(
    context: CoroutineContext? = null,
    crossinline block: suspend ErrorHandler.() -> T
): Either<Failure, T> {
    val execute = suspend {
        try {
            block().toRight()
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            } else {
                getFailure(e).toLeft()
            }
        }
    }

    return if (context != null) {
        withContext(context) { execute() }
    } else {
        execute()
    }
}

/**
 * Executes the [block] handling any [Exception]s that are thrown except [CancellationException]
 * to avoid interrupting the coroutine cancellation chain.
 *
 * It's main use case is to execute operations or side effects that shouldn't interrupt the main
 * work if they fail.
 *
 * @return the result of the [block] if no exceptions where caught, else null
 * */
suspend inline fun <T> ErrorHandler.ignoringFailure(
    context: CoroutineContext? = null,
    crossinline block: suspend ErrorHandler.() -> T
): T? {
    val execute = suspend {
        try {
            block()
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            } else {
                getFailure(e)
                null
            }
        }
    }

    return if (context != null) {
        withContext(context) { execute() }
    } else {
        execute()
    }
}