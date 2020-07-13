package me.juangoncalves.mentra.log

interface Logger {
    fun info(tag: String, message: Any?)
    fun warning(tag: String, message: Any?)
    fun error(tag: String, message: Any?)
}