package me.juangoncalves.mentra

import com.squareup.moshi.Moshi
import java.io.File

val moshi: Moshi by lazy { Moshi.Builder().build() }

fun Any.fixture(path: String): String {
    val url = this.javaClass.getResource(path)
    return File(url!!.file).readText()
}