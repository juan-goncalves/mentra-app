package me.juangoncalves.mentra.domain_layer.errors

import either.Either
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import kotlin.coroutines.CoroutineContext

interface ErrorHandler {

    fun getFailure(throwable: Throwable): Failure

}

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

suspend inline fun <T> ErrorHandler.ignoringFailure(
    context: CoroutineContext? = null,
    crossinline block: suspend ErrorHandler.() -> T
): T? {
    val execute = suspend {
        try {
            block()
        } catch (e: Exception) {
            getFailure(e)
            null
        }
    }

    return if (context != null) {
        withContext(context) { execute() }
    } else {
        execute()
    }
}