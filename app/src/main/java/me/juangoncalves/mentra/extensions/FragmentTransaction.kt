package me.juangoncalves.mentra.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import me.juangoncalves.mentra.R

fun FragmentTransaction.hide(tag: String, manager: FragmentManager): FragmentTransaction = apply {
    manager.findFragmentByTag(tag)?.let { fragment ->
        hide(fragment)
    }
}

fun FragmentTransaction.showExistingOrCreate(
    tag: String,
    fragment: Lazy<Fragment>,
    manager: FragmentManager
): FragmentTransaction = apply {
    val existingInstance = manager.findFragmentByTag(tag)
    if (existingInstance != null) {
        show(existingInstance)
    } else {
        add(R.id.fragmentContainer, fragment.value, tag)
    }
}