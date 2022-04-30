package me.juangoncalves.mentra.android_cache.entities

import androidx.room.*
import java.math.BigDecimal

@Entity(
    tableName = "Wallet",
    foreignKeys = [
        ForeignKey(
            entity = CoinEntity::class,
            parentColumns = ["symbol"],
            childColumns = ["coin_symbol"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("coin_symbol")]
)
data class WalletEntity(
    @ColumnInfo(name = "coin_symbol") val coinSymbol: String,
    @ColumnInfo(name = "amount") val amount: BigDecimal,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) {

    constructor(
        coinSymbol: String,
        amount: Double,
        id: Long = 0
    ) : this(coinSymbol, amount.toBigDecimal(), id)

}