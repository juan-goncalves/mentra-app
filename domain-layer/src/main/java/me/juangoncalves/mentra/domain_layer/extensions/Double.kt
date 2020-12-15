package me.juangoncalves.mentra.domain_layer.extensions

import kotlin.math.abs

infix fun Double.closeTo(other: Double): Boolean = abs(this - other) <= 0.001
