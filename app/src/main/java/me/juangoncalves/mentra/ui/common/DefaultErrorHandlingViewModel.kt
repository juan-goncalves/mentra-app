package me.juangoncalves.mentra.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.extensions.requireRight


abstract class DefaultErrorHandlingViewModel : ViewModel() {

    val defaultErrorStream: LiveData<DisplayError> get() = _defaultErrorStream

    private val _defaultErrorStream: MutableLiveData<DisplayError> = MutableLiveData()
    private val _retryChannel = Channel<Boolean>(1)

    fun errorSnackbarDismissed() {
        _retryChannel.offer(false)
    }

    protected fun <T> Flow<T>.showRetrySnackbarOnError(): Flow<T> {
        return retryWhen { cause, _ ->
            _defaultErrorStream.postValue(cause.toDisplayError())
            _retryChannel.receive()
        }
    }

    private fun Throwable.toDisplayError(): DisplayError {
        // TODO: Map the throwable to failures / error messages
        return DisplayError(R.string.default_error) {
            _retryChannel.offer(true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _retryChannel.close()
    }

    fun <P, R> UseCase<P, R>.prepare(): UseCaseExecutor<P, R> = UseCaseExecutor(this)

    inner class UseCaseExecutor<P, R>(val useCase: UseCase<P, R>) {
        private var _before: (suspend () -> Unit)? = null
        private var _after: (suspend () -> Unit)? = null
        private var _success: (suspend (R) -> Unit)? = null
        private var _dispatcher: CoroutineDispatcher = Dispatchers.Main

        fun beforeInvoke(func: (suspend () -> Unit)?) = apply { _before = func }

        fun afterInvoke(func: (suspend () -> Unit)?) = apply { _after = func }

        fun withDispatcher(dispatcher: CoroutineDispatcher) = apply { _dispatcher = dispatcher }

        fun onSuccess(func: (suspend (R) -> Unit)) = apply { _success = func }

        fun run(params: P) {
            viewModelScope.launch(_dispatcher) {
                _before?.invoke()

                val result = useCase(params)
                if (result.isLeft()) {
                    val error = DisplayError(me.juangoncalves.mentra.R.string.default_error) {
                        run(params)
                    }
                    _defaultErrorStream.postValue(error)
                } else {
                    _success?.invoke(result.requireRight())
                }

                _after?.invoke()
            }
        }
    }

}

