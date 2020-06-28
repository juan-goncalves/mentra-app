package me.juangoncalves.mentra.core.extensions

val Any.TAG: String
    get() = this::class.java.simpleName