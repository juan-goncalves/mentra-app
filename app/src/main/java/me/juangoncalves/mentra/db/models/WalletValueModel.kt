package me.juangoncalves.mentra.db.models

import androidx.room.*
import java.time.LocalDate

@Entity(
    tableName = "WalletValue",
    foreignKeys = [
        ForeignKey(
            entity = WalletModel::class,
            parentColumns = ["id"],
            childColumns = ["wallet_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("wallet_id")]
)
data class WalletValueModel(
    @ColumnInfo(name = "wallet_id") val walletId: Long,
    @ColumnInfo(name = "usd_value") val valueInUSD: Double,
    @PrimaryKey val date: LocalDate = LocalDate.now()
)
