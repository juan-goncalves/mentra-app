package me.juangoncalves.mentra.android_cache.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "PortfolioValue")
data class PortfolioValueModel(
    @ColumnInfo(name = "usd_value") val valueInUSD: BigDecimal,
    @PrimaryKey val date: LocalDate = LocalDate.now()
)