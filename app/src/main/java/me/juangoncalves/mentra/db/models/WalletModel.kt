package me.juangoncalves.mentra.db.models

import androidx.room.*

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
    @ColumnInfo(name = "amount") val amount: Double,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)