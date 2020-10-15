package me.juangoncalves.mentra.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface FleetingErrorPublisher {

    val fleetingErrorStream: LiveData<Event<DisplayError>>

    fun <P, R> UseCaseExecutor<P, R>.onFailurePublishFleetingError(): UseCaseExecutor<P, R>

}

class FleetingErrorPublisherImpl : FleetingErrorPublisher {

    override val fleetingErrorStream: LiveData<Event<DisplayError>> get() = _fleetingErrorStream

    private val _fleetingErrorStream: MutableLiveData<Event<DisplayError>> = MutableLiveData()

    override fun <P, R> UseCaseExecutor<P, R>.onFailurePublishFleetingError() = onFailure {
        _fleetingErrorStream.postValue(Event(it))
    }

}
