package me.juangoncalves.mentra

import java.io.File

fun Any.fixture(path: String): String {
    val url = this.javaClass.getResource(path)
    return File(url!!.file).readText()
}