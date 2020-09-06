package me.juangoncalves.mentra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.juangoncalves.mentra.db.daos.CoinDao
import me.juangoncalves.mentra.db.daos.CoinPriceDao
import me.juangoncalves.mentra.db.daos.WalletDao
import me.juangoncalves.mentra.db.models.CoinModel
import me.juangoncalves.mentra.db.models.CoinPriceModel
import me.juangoncalves.mentra.db.models.WalletModel

@Database(
    entities = [CoinModel::class, CoinPriceModel::class, WalletModel::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    abstract fun coinPriceDao(): CoinPriceDao

    abstract fun walletDao(): WalletDao

}