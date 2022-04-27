package me.juangoncalves.mentra.android_cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.math.BigDecimal
import java.time.LocalDate

@Entity(
    tableName = "WalletValue",
    primaryKeys = ["wallet_id", "date"],
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["wallet_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("wallet_id")
    ]
)
data class WalletValueEntity(
    @ColumnInfo(name = "wallet_id") val walletId: Long,
    @ColumnInfo(name = "usd_value") val valueInUSD: BigDecimal,
    @ColumnInfo(name = "date") val date: LocalDate = LocalDate.now()
)
