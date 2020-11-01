package me.juangoncalves.mentra.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.features.common.DisplayError
import me.juangoncalves.mentra.features.common.FleetingErrorPublisher

fun Fragment.createErrorSnackbar(
    error: DisplayError,
    anchor: View? = null,
    duration: Int = Snackbar.LENGTH_INDEFINITE
): Snackbar = with(requireContext()) { createErrorSnackbar(error, requireView(), duration, anchor) }

fun Fragment.showSnackbarOnFleetingErrors(
    fleetingErrorPublisher: FleetingErrorPublisher,
    anchor: View? = null
) {
    fleetingErrorPublisher.fleetingErrorStream.observe(viewLifecycleOwner) { errorEvent ->
        errorEvent.use { error ->
            createErrorSnackbar(error, anchor).show()
        }
    }
}