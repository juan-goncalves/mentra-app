package me.juangoncalves.mentra.extensions

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import me.juangoncalves.mentra.ui.common.DefaultErrorHandler

fun AppCompatActivity.showSnackbarOnDefaultErrors(
    viewModel: DefaultErrorHandler,
    view: View
) {
    viewModel.defaultErrorStream.observe(this) { error ->
        createErrorSnackbar(error, view)
            .onDismissed { viewModel.errorSnackbarDismissed() }
            .show()
    }
}