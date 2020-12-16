package me.juangoncalves.mentra.domain_layer.log

interface MentraLogger {
    fun info(tag: String, message: Any?)
    fun warning(tag: String, message: Any?)
    fun error(tag: String, message: Any?)
}