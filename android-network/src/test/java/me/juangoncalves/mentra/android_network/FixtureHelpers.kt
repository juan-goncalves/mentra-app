package me.juangoncalves.mentra.android_network

import com.squareup.moshi.Moshi
import java.io.File

internal val moshi: Moshi by lazy { Moshi.Builder().build() }

internal fun Any.fixture(path: String): String {
    val url = this.javaClass.getResource(path)
    return File(url!!.file).readText()
}