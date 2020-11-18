package me.juangoncalves.mentra.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import me.juangoncalves.mentra.R

fun FragmentTransaction.withFadeAnimation(): FragmentTransaction = apply {
    setCustomAnimations(
        R.animator.fade_in, R.animator.fade_out,
        R.animator.fade_in, R.animator.fade_out
    )
}

fun FragmentTransaction.addIfMissing(
    fragmentContainer: FragmentContainerView,
    fragment: Fragment,
    tag: String
): FragmentTransaction = apply {
    if (!fragment.isAdded) add(fragmentContainer.id, fragment, tag)
}

fun FragmentTransaction.hideAllFragmentsIn(manager: FragmentManager): FragmentTransaction = apply {
    manager.fragments.forEach { fragment ->
        hide(fragment)
    }
}