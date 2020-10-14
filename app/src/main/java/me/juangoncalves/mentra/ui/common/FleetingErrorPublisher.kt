package me.juangoncalves.mentra.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import me.juangoncalves.mentra.R

interface FleetingErrorPublisher {

    val fleetingErrorStream: LiveData<Event<DisplayError>>

    fun fleetingErrorDismissed()

    fun <T> Flow<T>.onFailurePublishFleetingError(): Flow<T>

    fun <P, R> UseCaseExecutor<P, R>.onFailurePublishFleetingError(): UseCaseExecutor<P, R>

    fun dispose()

}

class FleetingErrorPublisherImpl : FleetingErrorPublisher {

    override val fleetingErrorStream: LiveData<Event<DisplayError>> get() = _fleetingErrorStream

    private val _fleetingErrorStream: MutableLiveData<Event<DisplayError>> = MutableLiveData()
    private val _retryChannel = Channel<Boolean>(1)

    override fun fleetingErrorDismissed() {
        _retryChannel.offer(false)
    }

    override fun <T> Flow<T>.onFailurePublishFleetingError() = retryWhen { cause, _ ->
        val errorEvent = Event(cause.toDisplayError())
        _fleetingErrorStream.postValue(errorEvent)
        _retryChannel.receive()
    }

    private fun Throwable.toDisplayError(): DisplayError {
        return when (this) {
            else -> DisplayError(R.string.default_error) { _retryChannel.offer(true) }
        }
    }

    override fun <P, R> UseCaseExecutor<P, R>.onFailurePublishFleetingError() = onFailure {
        _fleetingErrorStream.postValue(Event(it))
    }

    override fun dispose() {
        _retryChannel.close()
    }

}
