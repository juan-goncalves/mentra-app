package me.juangoncalves.mentra.failures

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import me.juangoncalves.mentra.features.common.Event


/**
 * Meant to be implemented in [ViewModel]s using delegation to quickly handle errors
 * in conjunction with [FailureHandlingFragment] or [FailureHandlingDialogFragment].
 * */
interface FailureHandler {

    val fleetingErrorStream: LiveData<Event<FleetingError>>

    /** Executes the [UseCase] publishing a [FleetingError] in [fleetingErrorStream] if it fails. */
    suspend fun <Params, Result> UseCase<Params, Result>.runHandlingFailure(
        params: Params,
        onSuccess: (suspend (Result) -> Unit)? = null
    )

}
