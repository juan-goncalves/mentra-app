package me.juangoncalves.mentra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.juangoncalves.mentra.db.daos.*
import me.juangoncalves.mentra.db.models.*

@Database(
    entities = [
        CoinModel::class,
        CoinPriceModel::class,
        WalletModel::class,
        WalletValueModel::class,
        PortfolioValueModel::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    abstract fun coinPriceDao(): CoinPriceDao

    abstract fun walletDao(): WalletDao

    abstract fun walletValueDao(): WalletValueDao

    abstract fun portfolioDao(): PortfolioDao

}