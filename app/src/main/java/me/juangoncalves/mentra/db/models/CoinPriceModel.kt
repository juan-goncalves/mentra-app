package me.juangoncalves.mentra.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "CoinPrice",
    foreignKeys = [
        ForeignKey(
            entity = CoinModel::class,
            parentColumns = ["symbol"],
            childColumns = ["coin_symbol"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CoinPriceModel(
    @ColumnInfo(name = "coin_symbol") val coinSymbol: String,
    @ColumnInfo(name = "usd_value") val valueInUSD: Double,
    @PrimaryKey val timestamp: LocalDateTime = LocalDateTime.now()
)
