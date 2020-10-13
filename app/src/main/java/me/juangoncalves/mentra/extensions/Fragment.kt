package me.juangoncalves.mentra.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.ui.common.DefaultErrorHandler
import me.juangoncalves.mentra.ui.common.DisplayError

fun Fragment.createErrorSnackbar(
    error: DisplayError,
    anchor: View? = null,
    duration: Int = Snackbar.LENGTH_INDEFINITE
): Snackbar {
    val errorColor = requireContext().getThemeColor(R.attr.colorError)
    val onErrorColor = requireContext().getThemeColor(R.attr.colorOnError)

    return Snackbar.make(requireView(), error.messageId, duration)
        .setBackgroundTint(errorColor)
        .setTextColor(onErrorColor)
        .setAction(R.string.retry) { error.retryAction() }
        .setActionTextColor(onErrorColor)
        .apply {
            if (anchor != null) anchorView = anchor
        }
}

fun Fragment.showSnackbarOnDefaultErrors(
    viewModel: DefaultErrorHandler,
    anchor: View? = null
) {
    viewModel.defaultErrorStream.observe(viewLifecycleOwner) { error ->
        createErrorSnackbar(error, anchor)
            .onDismissed { viewModel.errorSnackbarDismissed() }
            .show()
    }
}