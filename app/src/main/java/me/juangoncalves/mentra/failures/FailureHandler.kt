package me.juangoncalves.mentra.failures

import androidx.lifecycle.LiveData
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import me.juangoncalves.mentra.features.common.Event


interface FailureHandler {

    val fleetingErrorStream: LiveData<Event<FleetingError>>

    suspend fun <Params, Result> UseCase<Params, Result>.runHandlingFailure(
        params: Params,
        onSuccess: (suspend (Result) -> Unit)? = null
    )

}
