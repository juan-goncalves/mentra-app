package me.juangoncalves.mentra.domain_layer.extensions

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlin.math.abs

fun LocalDateTime.Companion.now(): LocalDateTime {
    val currentMoment = Clock.System.now()
    return currentMoment.toLocalDateTime(currentSystemDefault())
}

fun LocalDateTime.minutesBetween(other: LocalDateTime): Long {
    val thisInstant = toInstant(currentSystemDefault())
    val otherInstant = other.toInstant(currentSystemDefault())
    val period = thisInstant.until(otherInstant, DateTimeUnit.MINUTE, currentSystemDefault())
    return abs(period)
}

fun LocalDateTime.daysBetween(other: LocalDateTime): Long {
    val thisInstant = toInstant(currentSystemDefault())
    val otherInstant = other.toInstant(currentSystemDefault())
    val period = otherInstant.until(thisInstant, DateTimeUnit.DAY, currentSystemDefault())
    return abs(period)
}

fun LocalDateTime.elapsedMinutes(): Long = this.minutesBetween(LocalDateTime.now())

fun LocalDateTime.elapsedDays(): Long = this.daysBetween(LocalDateTime.now())