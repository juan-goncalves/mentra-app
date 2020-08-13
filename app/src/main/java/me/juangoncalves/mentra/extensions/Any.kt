package me.juangoncalves.mentra.extensions

val Any.TAG: String
    get() = this::class.java.simpleName

fun empty() {}