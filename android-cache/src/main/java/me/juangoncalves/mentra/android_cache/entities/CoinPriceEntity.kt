package me.juangoncalves.mentra.android_cache.entities

import androidx.room.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(
    tableName = "CoinPrice",
    foreignKeys = [
        ForeignKey(
            entity = CoinEntity::class,
            parentColumns = ["symbol"],
            childColumns = ["coin_symbol"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("coin_symbol")]
)
data class CoinPriceEntity(
    @ColumnInfo(name = "coin_symbol") val coinSymbol: String,
    @ColumnInfo(name = "usd_value") val valueInUSD: BigDecimal,
    @PrimaryKey val timestamp: LocalDateTime = LocalDateTime.now()
)
