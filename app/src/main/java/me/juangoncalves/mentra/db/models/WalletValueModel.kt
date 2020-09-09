package me.juangoncalves.mentra.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate

@Entity(
    tableName = "WalletValue",
    primaryKeys = ["wallet_id", "date"],
    foreignKeys = [
        ForeignKey(
            entity = WalletModel::class,
            parentColumns = ["id"],
            childColumns = ["wallet_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("wallet_id")
    ]
)
data class WalletValueModel(
    @ColumnInfo(name = "wallet_id") val walletId: Long,
    @ColumnInfo(name = "usd_value") val valueInUSD: Double,
    @ColumnInfo(name = "date") val date: LocalDate = LocalDate.now()
)
