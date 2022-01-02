package me.juangoncalves.mentra.platform

import android.text.format.DateFormat
import java.util.*
import javax.inject.Inject

/** Wrapper to be able to mock the [DateFormat.getBestDateTimePattern] platform dependency. */
class DateTimePatternProvider @Inject constructor() {

    fun generatePattern(locale: Locale, skeleton: String): String {
        return DateFormat.getBestDateTimePattern(locale, skeleton)
    }
}