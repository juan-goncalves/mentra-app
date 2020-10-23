package me.juangoncalves.mentra.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


fun FragmentTransaction.show(
    fragment: Fragment,
    manager: FragmentManager,
    tag: String,
    @IdRes fragmentContainerId: Int
): FragmentTransaction = apply {
    val existingInstance = manager.findFragmentByTag(tag)
    if (existingInstance != null) {
        show(existingInstance)
    } else {
        add(fragmentContainerId, fragment, tag)
        show(fragment)
    }
}