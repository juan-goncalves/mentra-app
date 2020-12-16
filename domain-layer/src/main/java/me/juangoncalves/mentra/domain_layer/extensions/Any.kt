package me.juangoncalves.mentra.domain_layer.extensions

val Any.TAG: String
    get() = this::class.java.simpleName
