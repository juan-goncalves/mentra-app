package me.juangoncalves.mentra.extensions

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import me.juangoncalves.mentra.features.common.FleetingErrorPublisher

fun AppCompatActivity.showSnackbarOnFleetingErrors(
    fleetingErrorPublisher: FleetingErrorPublisher,
    view: View
) {
    fleetingErrorPublisher.fleetingErrorStream.observe(this) { errorEvent ->
        errorEvent.use { error ->
            createErrorSnackbar(error, view).show()
        }
    }
}