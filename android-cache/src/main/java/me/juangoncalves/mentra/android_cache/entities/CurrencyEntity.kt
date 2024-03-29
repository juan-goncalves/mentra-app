package me.juangoncalves.mentra.android_cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Currency")
data class CurrencyEntity(
    @PrimaryKey
    @ColumnInfo(name = "currency")
    val currency: Currency
)