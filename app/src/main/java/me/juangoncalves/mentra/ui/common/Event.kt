package me.juangoncalves.mentra.ui.common

open class Event<out T>(private val _content: T) {

    private var hasBeenConsumed = false

    val content: T?
        get() {
            return if (hasBeenConsumed) {
                null
            } else {
                hasBeenConsumed = true
                _content
            }
        }

}

class Notification : Event<Unit>(Unit)