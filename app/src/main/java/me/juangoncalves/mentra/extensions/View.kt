package me.juangoncalves.mentra.extensions

import android.view.View

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.updateVisibility(shouldShow: Boolean) {
    if (shouldShow) show() else hide()
}