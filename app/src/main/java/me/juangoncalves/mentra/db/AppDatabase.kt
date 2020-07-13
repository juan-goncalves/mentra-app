package me.juangoncalves.mentra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.juangoncalves.mentra.db.daos.CoinDao
import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.db.models.CoinPriceModel

@Database(
    entities = [CoinModel::class, CoinPriceModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

}