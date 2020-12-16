package me.juangoncalves.mentra.android_cache.models

import androidx.room.*
import java.math.BigDecimal

@Entity(
    tableName = "Wallet",
    foreignKeys = [
        ForeignKey(
            entity = CoinModel::class,
            parentColumns = ["symbol"],
            childColumns = ["coin_symbol"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("coin_symbol")]
)
data class WalletModel(
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