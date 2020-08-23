package me.juangoncalves.mentra.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.animateVisibility(shouldShow: Boolean, duration: Long = 700L) {
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