package me.juangoncalves.mentra.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import me.juangoncalves.mentra.core.db.AppDatabase
import me.juangoncalves.mentra.core.db.daos.CoinDao
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mentra.db" // TODO: Make build config variable for different flavors
        ).build()
    }

    @Provides
    fun provideCoinDao(database: AppDatabase): CoinDao {
        return database.coinDao()
    }

}