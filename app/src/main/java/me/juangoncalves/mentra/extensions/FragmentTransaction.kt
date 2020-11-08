package me.juangoncalves.mentra.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

/** Hides the received fragment if it has been added */
fun FragmentTransaction.hideIfAdded(fragment: Fragment): FragmentTransaction = apply {
    if (fragment.isAdded) hide(fragment)
}

/** Adds the received fragment if it hasn't been already added */
fun FragmentTransaction.addIfMissing(
    @IdRes containerId: Int,
    fragment: Fragment,
    tag: String
): FragmentTransaction = apply {
    if (!fragment.isAdded) add(containerId, fragment, tag)
}