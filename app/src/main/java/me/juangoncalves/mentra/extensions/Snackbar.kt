package me.juangoncalves.mentra.extensions

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import me.juangoncalves.mentra.R

fun Snackbar.onDismissed(func: (Int) -> Unit) = run {
    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)

            if (event != DISMISS_EVENT_ACTION) {
                func(event)
            }

            transientBottomBar?.removeCallback(this)
        }
    })
}

fun Snackbar.applyErrorStyle(): Snackbar = apply {
    val errorColor = context.getThemeColor(R.attr.colorError)
    val onErrorColor = context.getThemeColor(R.attr.colorOnError)

    setBackgroundTint(errorColor)
    setTextColor(onErrorColor)
    setActionTextColor(onErrorColor)
}