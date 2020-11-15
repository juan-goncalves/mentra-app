package me.juangoncalves.mentra.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.features.common.DisplayError
import me.juangoncalves.mentra.features.common.FleetingErrorPublisher

fun Fragment.createErrorSnackbar(
    error: DisplayError,
    view: View? = null,
    anchor: View? = null,
    duration: Int = Snackbar.LENGTH_LONG
): Snackbar = with(requireContext()) {
    createErrorSnackbar(error, view ?: requireView(), duration, anchor)
}

fun Fragment.showSnackbarOnFleetingErrors(
    fleetingErrorPublisher: FleetingErrorPublisher,
    view: View? = null,
    anchor: View? = null,
    duration: Int = Snackbar.LENGTH_LONG
) {
    fleetingErrorPublisher.fleetingErrorStream.observe(viewLifecycleOwner) { errorEvent ->
        errorEvent.use { error ->
            createErrorSnackbar(error, view, anchor, duration).show()
        }
    }
}

fun Fragment.hideKeyboard() = requireView().hideKeyboard()
