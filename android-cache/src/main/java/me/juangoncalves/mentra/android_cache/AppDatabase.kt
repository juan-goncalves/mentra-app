package me.juangoncalves.mentra.android_cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.juangoncalves.mentra.android_cache.daos.*
import me.juangoncalves.mentra.android_cache.models.*

@Database(
    entities = [
        CoinModel::class,
        CoinPriceModel::class,
        WalletModel::class,
        WalletValueModel::class,
        PortfolioValueModel::class,
        CurrencyEntity::class,
        ExchangeRateEntity::class
    ],
    version = 18,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    abstract fun coinPriceDao(): CoinPriceDao

    abstract fun walletDao(): WalletDao

    abstract fun walletValueDao(): WalletValueDao

    abstract fun portfolioDao(): PortfolioDao

    abstract fun exchangeRateDao(): CurrencyDao

}