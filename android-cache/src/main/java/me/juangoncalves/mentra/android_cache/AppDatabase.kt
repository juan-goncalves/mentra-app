package me.juangoncalves.mentra.android_cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.juangoncalves.mentra.android_cache.daos.*
import me.juangoncalves.mentra.android_cache.entities.*

@Database(
    entities = [
        CoinEntity::class,
        CoinPriceEntity::class,
        WalletEntity::class,
        WalletValueEntity::class,
        PortfolioValueEntity::class,
        CurrencyEntity::class,
        ExchangeRateEntity::class
    ],
    version = 1,
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