package me.juangoncalves.mentra.extensions

import me.juangoncalves.mentra.features.common.Event

fun empty() {}


fun <T> T.toEvent(): Event<T> = Event(this)