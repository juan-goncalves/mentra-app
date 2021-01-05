package me.juangoncalves.mentra.extensions

import me.juangoncalves.mentra.common.Event

fun empty() {}

fun <T> T.toEvent(): Event<T> = Event(this)