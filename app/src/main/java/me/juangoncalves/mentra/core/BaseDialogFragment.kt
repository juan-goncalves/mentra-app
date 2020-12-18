package me.juangoncalves.mentra.core

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.error.FleetingError
import me.juangoncalves.mentra.extensions.applyErrorStyle
import me.juangoncalves.mentra.extensions.empty

abstract class BaseDialogFragment<VM : BaseViewModel> : BottomSheetDialogFragment() {

    protected abstract val viewModel: VM

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureView()
        initObservers()
    }

    protected open fun configureView() = empty()

    @CallSuper
    /** Subscribe to the corresponding [LiveData] objects using [getViewLifecycleOwner] */
    protected open fun initObservers() {
        viewModel.fleetingErrorStream.observe(viewLifecycleOwner) { event ->
            event.use { fleetingError ->
                Snackbar.make(requireView(), fleetingError.message, Snackbar.LENGTH_LONG)
                    .setAnchorView(requireView())
                    .showRetryIfAvailable(fleetingError)
                    .applyErrorStyle()
                    .show()
            }
        }
    }

    private fun Snackbar.showRetryIfAvailable(fleetingError: FleetingError) = apply {
        if (fleetingError.retryAction != null) {
            setAction(R.string.retry) { viewModel.retryFailedAction(fleetingError) }
        }
    }

}