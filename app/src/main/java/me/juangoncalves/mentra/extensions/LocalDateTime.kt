package me.juangoncalves.mentra.extensions

import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs


fun LocalDateTime.minutesBetween(other: LocalDateTime): Long {
    val duration = Duration.between(this, other)
    return abs(duration.toMinutes())
}

fun LocalDateTime.daysBetween(other: LocalDateTime): Long {
    val duration = Duration.between(this, other)
    return abs(duration.toDays())
}

fun LocalDateTime.elapsedMinutes(): Long = this.minutesBetween(LocalDateTime.now())

fun LocalDateTime.elapsedDays(): Long = this.daysBetween(LocalDateTime.now())