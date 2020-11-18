package me.juangoncalves.mentra.features.common

import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.InternetConnectionFailure
import me.juangoncalves.mentra.domain.errors.WalletCreationFailure
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.requireRight

typealias Hook = suspend () -> Unit
typealias OnSuccess<T> = suspend (T) -> Unit
typealias OnFailure = suspend (DisplayError) -> Unit

class UseCaseExecutor<P, R>(val useCase: UseCase<P, R>) {
    private var _before: Hook? = null
    private var _after: Hook? = null
    private var _onSuccess: OnSuccess<R>? = null
    private var _onFailure: OnFailure? = null
    private var _dispatcher: CoroutineDispatcher = Dispatchers.Main
    private var _scope: CoroutineScope? = null

    fun withDispatcher(dispatcher: CoroutineDispatcher) = apply { _dispatcher = dispatcher }

    fun inScope(scope: CoroutineScope) = apply { _scope = scope }

    fun beforeInvoke(func: Hook) = apply { _before = func }

    fun afterInvoke(func: Hook) = apply { _after = func }

    fun onSuccess(func: OnSuccess<R>) = apply { _onSuccess = func }

    fun onFailure(func: OnFailure) = apply { _onFailure = func }

    fun run(params: P) {
        val safeScope = _scope
            ?: error("A coroutine scope must be specified using the `inScope` method to run this use case")

        safeScope.launch(_dispatcher) {
            _before?.invoke()

            val result = useCase(params)
            if (result.isLeft()) {
                val error = DisplayError(result.requireLeft().toStringResource()) {
                    run(params)
                }
                _onFailure?.invoke(error)
            } else {
                _onSuccess?.invoke(result.requireRight())
            }

            _after?.invoke()
        }
    }

    @StringRes
    private fun Failure.toStringResource(): Int {
        return when (this) {
            is InternetConnectionFailure -> me.juangoncalves.mentra.R.string.connection_error
            is WalletCreationFailure -> me.juangoncalves.mentra.R.string.create_wallet_error
            else -> me.juangoncalves.mentra.R.string.default_error
        }
    }

}

fun <R> UseCaseExecutor<Unit, R>.run() {
    run(Unit)
}

fun <P, R> UseCase<P, R>.executor(): UseCaseExecutor<P, R> = UseCaseExecutor(this)