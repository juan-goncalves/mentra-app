package me.juangoncalves.mentra.core

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.extensions.whenLeft
import me.juangoncalves.mentra.domain_layer.usecases.Interactor
import me.juangoncalves.mentra.error.FleetingError
import me.juangoncalves.mentra.extensions.toEvent
import me.juangoncalves.mentra.features.common.Event


abstract class BaseViewModel : ViewModel() {

    val fleetingErrorStream: LiveData<Event<FleetingError>> get() = _fleetingErrorStream
    private val _fleetingErrorStream: MutableLiveData<Event<FleetingError>> = MutableLiveData()

    fun retryFailedAction(error: FleetingError) {
        viewModelScope.launch {
            error.retryAction?.invoke()
        }
    }

    suspend fun <Params, Result> Interactor<Params, Result>.runHandlingFailure(
        params: Params,
        onSuccess: suspend (Result) -> Unit
    ) {
        val operation = invoke(params)

        operation.whenLeft { failure ->
            val error = FleetingError(getErrorMessage(failure)) {
                runHandlingFailure(params, onSuccess)
            }
            _fleetingErrorStream.postValue(error.toEvent())
        }

        operation.rightValue?.run { onSuccess(this) }
    }

    @StringRes
    private fun getErrorMessage(failure: Failure): Int {
        // TODO: Replace with actual string ids
        return when (failure) {
            Failure.Network -> R.string.connection_error
            Failure.NotFound -> R.string.default_error
            Failure.ServiceUnavailable -> R.string.default_error
            Failure.AccessDenied -> R.string.default_error
            Failure.Unknown -> R.string.default_error
        }
    }

}