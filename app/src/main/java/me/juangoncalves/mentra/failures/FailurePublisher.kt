package me.juangoncalves.mentra.failures

import androidx.lifecycle.LiveData
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import me.juangoncalves.mentra.features.common.Event


interface FailurePublisher {

    val fleetingErrorStream: LiveData<Event<FleetingError>>

    /** Executes the [UseCase] publishing a [FleetingError] in [fleetingErrorStream] if it fails. */
    suspend fun <Params, Result> UseCase<Params, Result>.runHandlingFailure(
        params: Params,
        onFailure: (suspend (Failure) -> Unit)? = null,
        onSuccess: (suspend (Result) -> Unit)? = null
    )

}
