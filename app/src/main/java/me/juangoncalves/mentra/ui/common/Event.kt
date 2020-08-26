package me.juangoncalves.mentra.ui.common

open class Event<out T>(private val content: T) {

    private var hasBeenConsumed = false

    fun getContent(): T? {
        return if (hasBeenConsumed) {
            null
        } else {
            hasBeenConsumed = true
            content
        }
    }

}