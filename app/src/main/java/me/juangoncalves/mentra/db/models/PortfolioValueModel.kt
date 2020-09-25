package me.juangoncalves.mentra.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "PortfolioValue")
data class PortfolioValueModel(
    @ColumnInfo(name = "usd_value") val valueInUSD: Double,
    @PrimaryKey val date: LocalDate = LocalDate.now()
)