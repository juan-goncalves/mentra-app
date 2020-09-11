package me.juangoncalves.pie

data class PiePortion(val percentage: Double, val text: String) : Comparable<PiePortion> {

    override fun compareTo(other: PiePortion): Int = percentage.compareTo(other.percentage)

}