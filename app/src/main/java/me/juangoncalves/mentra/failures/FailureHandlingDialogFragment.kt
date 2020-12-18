package me.juangoncalves.mentra.failures

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.extensions.applyErrorStyle

/** Automatically shows a [Snackbar] every time [FailureHandler.fleetingErrorStream] emits new values. */
abstract class FailureHandlingDialogFragment<T : FailureHandler> : BottomSheetDialogFragment() {

    protected abstract val viewModel: T

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.fleetingErrorStream.observe(viewLifecycleOwner) { event ->
            event.use { fleetingError ->
                Snackbar.make(requireView(), fleetingError.message, Snackbar.LENGTH_LONG)
                    .setAnchorView(requireView())
                    .applyErrorStyle()
                    .show()
            }
        }
    }

}