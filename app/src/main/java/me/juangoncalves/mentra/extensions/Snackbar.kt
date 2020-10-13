package me.juangoncalves.mentra.extensions

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

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