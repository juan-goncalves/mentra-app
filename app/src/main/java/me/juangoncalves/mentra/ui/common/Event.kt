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

    /**
     * Runs the received function if the event's content has not been consumed.
     */
    fun use(fn: (T) -> Unit) {
        content?.run(fn)
    }

}

class Notification : Event<Unit>(Unit)