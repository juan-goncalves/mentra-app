package me.juangoncalves.mentra.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.juangoncalves.mentra.domain_layer.models.IconType

@Entity(tableName = "Coin")
data class CoinModel(
    @PrimaryKey val symbol: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    val name: String,
    val iconType: IconType = IconType.Unknown
)
