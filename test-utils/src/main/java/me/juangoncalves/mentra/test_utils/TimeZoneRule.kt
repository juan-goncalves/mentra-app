package me.juangoncalves.mentra.test_utils

import org.junit.rules.ExternalResource
import java.util.*
import java.util.TimeZone.*

class TimeZoneTestRule(private val timeZone: String) : ExternalResource() {

    private lateinit var originalTimeZone: TimeZone

    override fun before() {
        super.before()
        originalTimeZone = getDefault()
        setDefault(getTimeZone(timeZone))
    }

    override fun after() {
        setDefault(originalTimeZone)
        super.after()
    }
}