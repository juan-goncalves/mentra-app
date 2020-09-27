package me.juangoncalves.mentra.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.R
import me.juangoncalves.mentra.ui.common.DisplayError

fun Fragment.createErrorSnackbar(error: DisplayError, anchor: View = requireView()): Snackbar {
    val errorColor = requireContext().getThemeColor(R.attr.colorError)
    val onErrorColor = requireContext().getThemeColor(R.attr.colorOnError)

    return Snackbar.make(anchor, error.messageId, Snackbar.LENGTH_LONG)
        .setBackgroundTint(errorColor)
        .setTextColor(onErrorColor)
        .setAction(R.string.retry) { error.retryAction() }
        .setAnchorView(anchor)
        .setActionTextColor(onErrorColor)
}
