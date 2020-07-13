package me.juangoncalves.mentra.db.models

import androidx.room.*
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
    ],
    indices = [Index("coin_symbol")]
)
data class CoinPriceModel(
    @ColumnInfo(name = "coin_symbol") val coinSymbol: String,
    @ColumnInfo(name = "usd_value") val valueInUSD: Double,
    @PrimaryKey val timestamp: LocalDateTime = LocalDateTime.now()
)
