package me.juangoncalves.mentra.domain_layer.models

sealed class TimeGranularity(val name: String) {
    object Daily : TimeGranularity("Daily")
    object Weekly : TimeGranularity("Weekly")
    object Monthly : TimeGranularity("Monthly")
}