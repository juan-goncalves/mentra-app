package me.juangoncalves.mentra.features.stats.model

import com.github.mikephil.charting.data.Entry
import me.juangoncalves.mentra.domain.models.TimeGranularity

data class TimeChartData(
    val entries: List<Entry>,
    val labels: List<String>,
    val granularity: TimeGranularity
)