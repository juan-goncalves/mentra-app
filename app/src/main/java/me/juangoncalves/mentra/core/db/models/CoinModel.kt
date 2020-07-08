package me.juangoncalves.mentra.core.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Coin")
data class CoinModel(
    @PrimaryKey val symbol: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    val name: String
)
