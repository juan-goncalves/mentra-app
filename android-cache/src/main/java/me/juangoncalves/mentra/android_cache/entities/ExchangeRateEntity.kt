package me.juangoncalves.mentra.android_cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "ExchangeRate",
    primaryKeys = ["base_currency_symbol", "target_currency_symbol"]
)
data class ExchangeRateEntity(
    @ColumnInfo(name = "base_currency_symbol")
    val base: Currency,
    @ColumnInfo(name = "target_currency_symbol")
    val target: Currency,
    @ColumnInfo(name = "rate")
    val rate: BigDecimal,
    @ColumnInfo(name = "timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
)