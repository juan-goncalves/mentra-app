package me.juangoncalves.mentra.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.animateVisibility(shouldShow: Boolean, duration: Long = 700L) {
    when {
        // Ensure that the view is visible to show the alpha animation
        visibility == View.GONE && shouldShow -> visibility = View.VISIBLE
        // If the view is already gone we don't have to animate it fading out
        !shouldShow && visibility == View.GONE -> return
    }

    clearAnimation()
    animate()
        .alpha(if (shouldShow) 1f else 0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = if (alpha == 0f) View.GONE else View.VISIBLE
            }
        })
        .start()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}