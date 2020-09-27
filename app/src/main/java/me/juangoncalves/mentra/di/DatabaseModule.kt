package me.juangoncalves.mentra.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.juangoncalves.mentra.db.AppDatabase
import me.juangoncalves.mentra.db.daos.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "mentra.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCoinDao(database: AppDatabase): CoinDao = database.coinDao()

    @Provides
    fun provideCoinPriceDao(database: AppDatabase): CoinPriceDao = database.coinPriceDao()

    @Provides
    fun provideWalletDao(database: AppDatabase): WalletDao = database.walletDao()

    @Provides
    fun provideWalletValueDao(database: AppDatabase): WalletValueDao = database.walletValueDao()

    @Provides
    fun providePortfolioDao(database: AppDatabase): PortfolioDao = database.portfolioDao()

}