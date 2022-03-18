package me.juangoncalves.mentra.android_cache.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Coin")
data class CoinModel(
    @PrimaryKey val symbol: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    val name: String,
    val position: Int = Int.MAX_VALUE,
)
