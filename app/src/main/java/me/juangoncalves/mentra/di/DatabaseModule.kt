package me.juangoncalves.mentra.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.juangoncalves.mentra.db.AppDatabase
import me.juangoncalves.mentra.db.daos.CoinDao
import me.juangoncalves.mentra.db.daos.WalletDao
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
    fun provideWalletDao(database: AppDatabase): WalletDao = database.walletDao()

}