package me.juangoncalves.mentra.core.log

import android.util.Log
import javax.inject.Inject

class AndroidLogger @Inject constructor() : Logger {

    override fun info(tag: String, message: Any?) {
        Log.i(tag, message.toString())
    }

    override fun warning(tag: String, message: Any?) {
        Log.w(tag, message.toString())
    }

    override fun error(tag: String, message: Any?) {
        Log.e(tag, message.toString())
    }

}