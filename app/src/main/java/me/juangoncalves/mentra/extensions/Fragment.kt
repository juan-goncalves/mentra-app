package me.juangoncalves.mentra.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.failures.FailurePublisher

fun Fragment.hideKeyboard() = requireView().hideKeyboard()

fun Fragment.handleErrorsFrom(
    failurePublisher: FailurePublisher,
    anchor: View? = null
) {
    failurePublisher.fleetingErrorStream.observe(viewLifecycleOwner) { event ->
        event.use { fleetingError ->
            Snackbar.make(requireView(), fleetingError.message, Snackbar.LENGTH_LONG)
                .applyErrorStyle()
                .apply {
                    if (anchor != null) {
                        anchorView = anchor
                    }
                }
                .show()
        }
    }
}