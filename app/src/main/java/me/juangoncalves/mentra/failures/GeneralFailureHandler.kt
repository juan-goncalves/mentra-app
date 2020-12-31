package me.juangoncalves.mentra.failures

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.extensions.whenLeft
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import me.juangoncalves.mentra.extensions.toEvent
import me.juangoncalves.mentra.features.common.Event

class GeneralFailureHandler : FailureHandler {

    override val fleetingErrorStream: LiveData<Event<FleetingError>> get() = _fleetingErrorStream
    private val _fleetingErrorStream: MutableLiveData<Event<FleetingError>> = MutableLiveData()

    override suspend fun <Params, Result> UseCase<Params, Result>.runHandlingFailure(
        params: Params,
        onSuccess: (suspend (Result) -> Unit)?
    ) {
        val operation = invoke(params)

        operation.whenLeft { failure ->
            val error = FleetingError(getErrorMessage(failure))
            _fleetingErrorStream.postValue(error.toEvent())
        }

        operation.rightValue?.run { onSuccess?.invoke(this) }
    }

    @StringRes
    private fun getErrorMessage(failure: Failure): Int {
        return when (failure) {
            Failure.Network -> R.string.connection_error
            Failure.NotFound -> R.string.not_found
            Failure.ServiceUnavailable -> R.string.service_temporarily_unavailable
            Failure.AccessDenied -> R.string.access_denied
            Failure.Unknown -> R.string.default_error
            Failure.InvalidRequest -> R.string.default_error
        }
    }

}